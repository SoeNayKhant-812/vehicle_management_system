package com.example.demo.service;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Motorcycle;
import com.example.demo.repository.MotorcycleRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;

@Service
public class MotorcycleService {

	private static final Logger logger = LoggerFactory.getLogger(MotorcycleService.class);

	@Autowired
	private MotorcycleRepository motorcycleRepository;

	@Autowired
	private IdGeneratorService idGenerator;

	public List<Motorcycle> getAllMotorcycles() {
		logger.info("Fetching all motorcycles from the database.");
		return motorcycleRepository.findAll();
	}

	public Motorcycle getMotorcycleById(String id) {
		logger.info("Fetching motorcycle with ID: {}", id);
		return motorcycleRepository.findById(id).orElseThrow(() -> {
			logger.warn("Motorcycle not found with ID: {}", id);
			return new VehicleNotFoundException("Motorcycle not found with ID: " + id);
		});
	}

	public Motorcycle addMotorcycle(MotorcycleDTO dto) {
		String generatedId = idGenerator.generateMotorcycleId();
		Motorcycle motorcycle = new Motorcycle();
		motorcycle.setId(generatedId);
		motorcycle.setBrand(dto.getBrand());
		motorcycle.setModel(dto.getModel());
		motorcycle.setCreatedAt(Instant.now());

		logger.info("Adding new motorcycle [ID={}, Brand={}, Model={}]", generatedId, dto.getBrand(), dto.getModel());

		return motorcycleRepository.save(motorcycle);
	}

	public Motorcycle updateMotorcycle(String id, MotorcycleDTO dto) {
		logger.info("Attempting to update motorcycle with ID: {}", id);
		Motorcycle existing = motorcycleRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. Motorcycle not found with ID: {}", id);
			return new VehicleNotFoundException("Motorcycle with ID " + id + " not found");
		});

		existing.setBrand(dto.getBrand());
		existing.setModel(dto.getModel());

		logger.info("Successfully updated motorcycle with ID: {}", id);
		return motorcycleRepository.save(existing);
	}

	public void deleteMotorcycle(String id) {
		logger.info("Attempting to delete motorcycle with ID: {}", id);
		if (!motorcycleRepository.existsById(id)) {
			logger.warn("Cannot delete. Motorcycle not found with ID: {}", id);
			throw new VehicleNotFoundException("Cannot delete. Motorcycle not found with ID: " + id);
		}

		motorcycleRepository.deleteById(id);
		logger.info("Successfully deleted motorcycle with ID: {}", id);
	}
}
