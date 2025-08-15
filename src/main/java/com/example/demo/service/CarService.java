package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.exception.TransactionFailureException;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.model.User;
import com.example.demo.model.log_model.CarLog;
import com.example.demo.repository.CarRepository;
import com.example.demo.service.log_service.CarLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.stereotype.Service;
import com.example.demo.config.RedisCacheConfig;

import java.time.Instant;
import java.util.List;

@Service
public class CarService {

	private static final Logger logger = LoggerFactory.getLogger(CarService.class);

	private final CarRepository carRepository;
	private final IdGeneratorService idGenerator;
	private final CarLogService carLogService;
	private final UserService userService;

	public CarService(CarRepository carRepository, IdGeneratorService idGenerator, CarLogService carLogService,
			UserService userService) {
		this.carRepository = carRepository;
		this.idGenerator = idGenerator;
		this.carLogService = carLogService;
		this.userService = userService;
	}

	private User getCurrentUser() {
		return userService.getCurrentUserOrThrow();
	}

	@Cacheable(RedisCacheConfig.CARS_CACHE) // Uses "cars" cache (5 min TTL)
	public List<Car> getAllCars() {
		logger.info("Fetching all cars from the database.");
		return carRepository.findAll();
	}

	@Cacheable(value = RedisCacheConfig.CAR_CACHE, key = "#id") // Uses "car" cache (1 hour TTL)
	public Car getCarById(String id) {
		logger.info("Fetching car with ID: {}", id);
		return carRepository.findById(id).orElseThrow(() -> {
			logger.warn("Car not found with ID: {}", id);
			return new VehicleNotFoundException("Car not found with ID: " + id);
		});
	}

	// CREATE with DynamoDB transaction (car + log)
	@Caching(put = { @CachePut(value = RedisCacheConfig.CAR_CACHE, key = "#result.id") }, // Puts new car in "car" cache
			evict = { @CacheEvict(value = RedisCacheConfig.CARS_CACHE, allEntries = true) } // Clears "cars" cache
	)
	public Car addCar(CarDTO dto) throws TransactionFailureException {
		validateCarDTO(dto);

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		String generatedId = idGenerator.generateCarId();
		Car car = new Car();
		car.setId(generatedId);
		car.setBrand(dto.getBrand().trim());
		car.setModel(dto.getModel().trim());
		car.setCreatedAt(Instant.now());

		CarLog log = carLogService.buildCarLog(car, "CREATE", performedByUserId, performedByUsername);

		logger.info("Creating new car & log [carId={}, logId={}]", car.getId(), log.getId());

		try {
			return carRepository.saveWithLog(car, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to create car with transactional log for carId={}: {}", car.getId(), ex.getMessage(),
					ex);
			throw ex;
		}
	}

	// UPDATE with transaction (update + log)
	@Caching(put = { @CachePut(value = RedisCacheConfig.CAR_CACHE, key = "#id") }, // Updates car in "car" cache
			evict = { @CacheEvict(value = RedisCacheConfig.CARS_CACHE, allEntries = true) } // Clears "cars" cache
	)
	public Car updateCar(String id, CarDTO dto) throws TransactionFailureException {
		validateCarDTO(dto);

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		Car existing = carRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. Car not found with ID: {}", id);
			return new VehicleNotFoundException("Car with ID " + id + " not found");
		});

		existing.setBrand(dto.getBrand().trim());
		existing.setModel(dto.getModel().trim());

		CarLog log = carLogService.buildCarLog(existing, "UPDATE", performedByUserId, performedByUsername);

		logger.info("Updating car & writing log [carId={}, logId={}]", existing.getId(), log.getId());

		try {
			return carRepository.updateWithLog(existing, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to update car with transactional log for carId={}: {}", existing.getId(),
					ex.getMessage(), ex);
			throw ex;
		}
	}

	// DELETE with transaction (delete + log)
	@Caching(evict = { @CacheEvict(value = RedisCacheConfig.CAR_CACHE, key = "#id"), // Removes from "car" cache
			@CacheEvict(value = RedisCacheConfig.CARS_CACHE, allEntries = true) // Clears "cars" cache
	})
	public void deleteCar(String id) throws TransactionFailureException {
		Car existing = carRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot delete. Car not found with ID: {}", id);
			return new VehicleNotFoundException("Cannot delete. Car not found with ID: " + id);
		});

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		CarLog log = carLogService.buildCarLog(existing, "DELETE", performedByUserId, performedByUsername);

		logger.info("Deleting car & writing log [carId={}, logId={}]", existing.getId(), log.getId());

		try {
			carRepository.deleteWithLog(existing, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to delete car with transactional log for carId={}: {}", existing.getId(),
					ex.getMessage(), ex);
			throw ex;
		}
	}

	private void validateCarDTO(CarDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("Car data is required");
		}
		if (dto.getBrand() == null || dto.getBrand().trim().isEmpty()) {
			throw new IllegalArgumentException("Car brand is required");
		}
		if (dto.getModel() == null || dto.getModel().trim().isEmpty()) {
			throw new IllegalArgumentException("Car model is required");
		}
	}
}
