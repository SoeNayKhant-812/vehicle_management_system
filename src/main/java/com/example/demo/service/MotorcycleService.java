package com.example.demo.service;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Motorcycle;
import com.example.demo.repository.MotorcycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

@Service
public class MotorcycleService {

	private static final Logger logger = Logger.getLogger(MotorcycleService.class.getName());

	@Autowired
	private MotorcycleRepository motorcycleRepository;

	@Autowired
	private VehicleIdGeneratorService idGenerator;

	public List<Motorcycle> getAllMotorcycles() {
		return motorcycleRepository.findAll();
	}

	public Motorcycle getMotorcycleById(String id) {
		return motorcycleRepository.findById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Motorcycle not found with ID: " + id));
	}

	public Motorcycle addMotorcycle(MotorcycleDTO dto) {
		Motorcycle motorcycle = new Motorcycle();
		String generatedId = idGenerator.generateMotorcycleId();
		motorcycle.setId(generatedId);
		motorcycle.setBrand(dto.getBrand());
		motorcycle.setModel(dto.getModel());
		motorcycle.setCreatedAt(Instant.now());

		logger.info("Generated new Motorcycle ID: " + generatedId);

		return motorcycleRepository.save(motorcycle);
	}

	public Motorcycle updateMotorcycle(String id, MotorcycleDTO dto) {
		Motorcycle existingMotorcycle = motorcycleRepository.findById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Motorcycle with ID " + id + " not found"));

		existingMotorcycle.setBrand(dto.getBrand());
		existingMotorcycle.setModel(dto.getModel());

		return motorcycleRepository.save(existingMotorcycle);
	}

	public void deleteMotorcycle(String id) {
		if (!motorcycleRepository.existsById(id)) {
			throw new VehicleNotFoundException("Cannot delete. Motorcycle not found with ID: " + id);
		}
		motorcycleRepository.deleteById(id);
	}
}
