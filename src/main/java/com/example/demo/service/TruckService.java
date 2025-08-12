package com.example.demo.service;

import com.example.demo.dto.TruckDTO;
import com.example.demo.exception.TransactionFailureException;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Truck;
import com.example.demo.model.User;
import com.example.demo.model.log_model.TruckLog;
import com.example.demo.repository.TruckRepository;
import com.example.demo.service.log_service.TruckLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
public class TruckService {

	private static final Logger logger = LoggerFactory.getLogger(TruckService.class);

	private final TruckRepository truckRepository;
	private final IdGeneratorService idGenerator;
	private final TruckLogService truckLogService;
	private final UserService userService;

	public TruckService(TruckRepository truckRepository, IdGeneratorService idGenerator,
			TruckLogService truckLogService, UserService userService) {
		this.truckRepository = truckRepository;
		this.idGenerator = idGenerator;
		this.truckLogService = truckLogService;
		this.userService = userService;
	}

	private User getCurrentUser() {
		return userService.getCurrentUserOrThrow();
	}

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

	public Truck addTruck(TruckDTO dto) throws TransactionFailureException {
		validateTruckDTO(dto);

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		String generatedId = idGenerator.generateTruckId();
		Truck truck = new Truck();
		truck.setId(generatedId);
		truck.setBrand(dto.getBrand().trim());
		truck.setModel(dto.getModel().trim());
		truck.setCreatedAt(Instant.now());

		TruckLog log = truckLogService.buildTruckLog(truck, "CREATE", performedByUserId, performedByUsername);

		logger.info("Creating new truck & log [truckId={}, logId={}]", truck.getId(), log.getId());

		try {
			return truckRepository.saveWithLog(truck, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to create truck with transactional log for id={}: {}", truck.getId(), ex.getMessage(),
					ex);
			throw ex;
		}
	}

	public Truck updateTruck(String id, TruckDTO dto) throws TransactionFailureException {
		validateTruckDTO(dto);

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		Truck existing = truckRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot update. Truck not found with ID: {}", id);
			return new VehicleNotFoundException("Truck with ID " + id + " not found");
		});

		existing.setBrand(dto.getBrand().trim());
		existing.setModel(dto.getModel().trim());

		TruckLog log = truckLogService.buildTruckLog(existing, "UPDATE", performedByUserId, performedByUsername);

		logger.info("Updating truck & writing log [truckId={}, logId={}]", existing.getId(), log.getId());

		try {
			return truckRepository.updateWithLog(existing, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to update truck with transactional log for id={}: {}", existing.getId(),
					ex.getMessage(), ex);
			throw ex;
		}
	}

	public void deleteTruck(String id) throws TransactionFailureException {
		Truck existing = truckRepository.findById(id).orElseThrow(() -> {
			logger.warn("Cannot delete. Truck not found with ID: {}", id);
			return new VehicleNotFoundException("Cannot delete. Truck not found with ID: " + id);
		});

		final User currentUser = getCurrentUser();
		final String performedByUserId = currentUser.getId();
		final String performedByUsername = currentUser.getUsername();

		TruckLog log = truckLogService.buildTruckLog(existing, "DELETE", performedByUserId, performedByUsername);

		logger.info("Deleting truck & writing log [truckId={}, logId={}]", existing.getId(), log.getId());

		try {
			truckRepository.deleteWithLog(existing, log);
		} catch (RuntimeException ex) {
			logger.error("Failed to delete truck with transactional log for id={}: {}", existing.getId(),
					ex.getMessage(), ex);
			throw ex;
		}
	}

	private void validateTruckDTO(TruckDTO dto) {
		if (dto == null) {
			throw new IllegalArgumentException("Truck data is required");
		}
		if (dto.getBrand() == null || dto.getBrand().trim().isEmpty()) {
			throw new IllegalArgumentException("Truck brand is required");
		}
		if (dto.getModel() == null || dto.getModel().trim().isEmpty()) {
			throw new IllegalArgumentException("Truck model is required");
		}
	}
}
