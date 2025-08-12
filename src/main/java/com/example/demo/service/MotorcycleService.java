package com.example.demo.service;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.exception.TransactionFailureException;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Motorcycle;
import com.example.demo.model.User;
import com.example.demo.model.log_model.MotorcycleLog;
import com.example.demo.repository.MotorcycleRepository;
import com.example.demo.service.log_service.MotorcycleLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class MotorcycleService {

	private static final Logger logger = LoggerFactory.getLogger(MotorcycleService.class);

	private final MotorcycleRepository motorcycleRepository;
	private final IdGeneratorService idGenerator;
	private final MotorcycleLogService motorcycleLogService;
	private final UserService userService;

	public MotorcycleService(MotorcycleRepository motorcycleRepository, IdGeneratorService idGenerator,
			MotorcycleLogService motorcycleLogService, UserService userService) {
		this.motorcycleRepository = motorcycleRepository;
		this.idGenerator = idGenerator;
		this.motorcycleLogService = motorcycleLogService;
		this.userService = userService;
	}

	private User getCurrentUser() {
		return userService.getCurrentUserOrThrow();
	}

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

	public Motorcycle addMotorcycle(MotorcycleDTO dto) throws TransactionFailureException {
		validateMotorcycleDTO(dto);

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		String generatedId = idGenerator.generateMotorcycleId();
		Motorcycle motorcycle = new Motorcycle();
		motorcycle.setId(generatedId);
		motorcycle.setBrand(dto.getBrand().trim());
		motorcycle.setModel(dto.getModel().trim());
		motorcycle.setCreatedAt(Instant.now());

		MotorcycleLog log = motorcycleLogService.buildMotorcycleLog(motorcycle, "CREATE", performedByUserId,
				performedByUsername);

		logger.info("Creating new motorcycle & log [motorcycleId={}, logId={}]", motorcycle.getId(), log.getId());

		try {
			return motorcycleRepository.saveWithLog(motorcycle, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to create motorcycle with transactional log for id={}: {}", motorcycle.getId(),
					ex.getMessage(), ex);
			throw ex;
		}
	}

	public Motorcycle updateMotorcycle(String id, MotorcycleDTO dto) throws TransactionFailureException {
		validateMotorcycleDTO(dto);

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		Motorcycle existing = motorcycleRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. Motorcycle not found with ID: {}", id);
			return new VehicleNotFoundException("Motorcycle with ID " + id + " not found");
		});

		existing.setBrand(dto.getBrand().trim());
		existing.setModel(dto.getModel().trim());

		MotorcycleLog log = motorcycleLogService.buildMotorcycleLog(existing, "UPDATE", performedByUserId,
				performedByUsername);

		logger.info("Updating motorcycle & writing log [motorcycleId={}, logId={}]", existing.getId(), log.getId());

		try {
			return motorcycleRepository.updateWithLog(existing, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to update motorcycle with transactional log for id={}: {}", existing.getId(),
					ex.getMessage(), ex);
			throw ex;
		}
	}

	public void deleteMotorcycle(String id) throws TransactionFailureException {
		Motorcycle existing = motorcycleRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot delete. Motorcycle not found with ID: {}", id);
			return new VehicleNotFoundException("Cannot delete. Motorcycle not found with ID: " + id);
		});

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		MotorcycleLog log = motorcycleLogService.buildMotorcycleLog(existing, "DELETE", performedByUserId,
				performedByUsername);

		logger.info("Deleting motorcycle & writing log [motorcycleId={}, logId={}]", existing.getId(), log.getId());

		try {
			motorcycleRepository.deleteWithLog(existing, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to delete motorcycle with transactional log for id={}: {}", existing.getId(),
					ex.getMessage(), ex);
			throw ex;
		}
	}

	private void validateMotorcycleDTO(MotorcycleDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("Motorcycle data is required");
		}
		if (dto.getBrand() == null || dto.getBrand().trim().isEmpty()) {
			throw new IllegalArgumentException("Motorcycle brand is required");
		}
		if (dto.getModel() == null || dto.getModel().trim().isEmpty()) {
			throw new IllegalArgumentException("Motorcycle model is required");
		}
	}
}
