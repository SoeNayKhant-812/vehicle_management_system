package com.example.demo.service;

import com.example.demo.dto.TruckDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Truck;
import com.example.demo.repository.TruckRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Service
public class TruckService {

	private static final Logger logger = LoggerFactory.getLogger(TruckService.class);

	@Autowired
	private TruckRepository truckRepository;

	@Autowired
	private VehicleIdGeneratorService idGenerator;

	public List<Truck> getAllTrucks() {
		logger.info("Fetching all trucks from the database.");
		return truckRepository.findAll();
	}

	public Truck getTruckById(String id) {
		logger.info("Fetching truck with ID: {}", id);
		return truckRepository.findById(id).orElseThrow(() -> {
			logger.warn("Truck not found with ID: {}", id);
			return new VehicleNotFoundException("Truck not found with ID: " + id);
		});
	}

	public Truck addTruck(TruckDTO dto) {
		String generatedId = idGenerator.generateTruckId();
		Truck truck = new Truck();
		truck.setId(generatedId);
		truck.setBrand(dto.getBrand());
		truck.setModel(dto.getModel());
		truck.setCreatedAt(Instant.now());

		logger.info("Adding new truck [ID={}, Brand={}, Model={}]", generatedId, dto.getBrand(), dto.getModel());

		return truckRepository.save(truck);
	}

	public Truck updateTruck(String id, TruckDTO dto) {
		logger.info("Attempting to update truck with ID: {}", id);
		Truck existing = truckRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. Truck not found with ID: {}", id);
			return new VehicleNotFoundException("Truck with ID " + id + " not found");
		});

		existing.setBrand(dto.getBrand());
		existing.setModel(dto.getModel());

		logger.info("Successfully updated truck with ID: {}", id);
		return truckRepository.save(existing);
	}

	public void deleteTruck(String id) {
		logger.info("Attempting to delete truck with ID: {}", id);
		if (!truckRepository.existsById(id)) {
			logger.warn("Cannot delete. Truck not found with ID: {}", id);
			throw new VehicleNotFoundException("Cannot delete. Truck not found with ID: " + id);
		}

		truckRepository.deleteById(id);
		logger.info("Successfully deleted truck with ID: {}", id);
	}
}
