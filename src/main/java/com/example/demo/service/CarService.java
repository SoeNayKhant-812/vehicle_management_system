package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

@Service
public class CarService {

	private static final Logger logger = Logger.getLogger(CarService.class.getName());

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private VehicleIdGeneratorService idGenerator;

	public List<Car> getAllCars() {
		return carRepository.findAll();
	}

	public Car getCarById(String id) {
		return carRepository.findById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Car not found with ID: " + id));
	}

	public Car addCar(CarDTO dto) {
		Car car = new Car();
		String generatedId = idGenerator.generateCarId();
		car.setId(generatedId);
		car.setBrand(dto.getBrand());
		car.setModel(dto.getModel());
		car.setCreatedAt(Instant.now());

		logger.info("Generated new Car ID: " + generatedId);

		return carRepository.save(car);
	}

	public Car updateCar(String id, CarDTO dto) {
		Car existingCar = carRepository.findById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Car with ID " + id + " not found"));

		existingCar.setBrand(dto.getBrand());
		existingCar.setModel(dto.getModel());

		return carRepository.save(existingCar);
	}

	public void deleteCar(String id) {
		if (!carRepository.existsById(id)) {
			throw new VehicleNotFoundException("Cannot delete. Car not found with ID: " + id);
		}
		carRepository.deleteById(id);
	}
}
