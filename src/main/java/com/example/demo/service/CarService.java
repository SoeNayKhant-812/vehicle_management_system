package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Service
public class CarService {

	private static final Logger logger = LoggerFactory.getLogger(CarService.class);

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private VehicleIdGeneratorService idGenerator;

	public List<Car> getAllCars() {
		logger.info("Fetching all cars from the database.");
		return carRepository.findAll();
	}

	public Car getCarById(String id) {
		logger.info("Fetching car with ID: {}", id);
		return carRepository.findById(id).orElseThrow(() -> {
			logger.warn("Car not found with ID: {}", id);
			return new VehicleNotFoundException("Car not found with ID: " + id);
		});
	}

	public Car addCar(CarDTO dto) {
		String generatedId = idGenerator.generateCarId();
		Car car = new Car();
		car.setId(generatedId);
		car.setBrand(dto.getBrand());
		car.setModel(dto.getModel());
		car.setCreatedAt(Instant.now());

		logger.info("Creating new car [ID={}, Brand={}, Model={}]", generatedId, dto.getBrand(), dto.getModel());

		return carRepository.save(car);
	}

	public Car updateCar(String id, CarDTO dto) {
		logger.info("Attempting to update car with ID: {}", id);
		Car existingCar = carRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. Car not found with ID: {}", id);
			return new VehicleNotFoundException("Car with ID " + id + " not found");
		});

		existingCar.setBrand(dto.getBrand());
		existingCar.setModel(dto.getModel());

		logger.info("Successfully updated car with ID: {}", id);
		return carRepository.save(existingCar);
	}

	public void deleteCar(String id) {
		logger.info("Attempting to delete car with ID: {}", id);
		if (!carRepository.existsById(id)) {
			logger.warn("Cannot delete. Car not found with ID: {}", id);
			throw new VehicleNotFoundException("Cannot delete. Car not found with ID: " + id);
		}

		carRepository.deleteById(id);
		logger.info("Successfully deleted car with ID: {}", id);
	}
}
