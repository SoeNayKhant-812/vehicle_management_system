package com.example.demo.service.log_service;

import com.example.demo.model.Motorcycle;
import com.example.demo.model.log_model.MotorcycleLog;
import com.example.demo.repository.log_repository.MotorcycleLogRepository;
import com.example.demo.repository.log_repository.MotorcycleLogRepository.Holder;
import com.example.demo.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.*;

@Service
public class MotorcycleLogService {

	private final MotorcycleLogRepository motorcycleLogRepository;
	private final IdGeneratorService idGeneratorService;

	@Autowired
	public MotorcycleLogService(MotorcycleLogRepository motorcycleLogRepository,
			IdGeneratorService idGeneratorService) {
		this.motorcycleLogRepository = motorcycleLogRepository;
		this.idGeneratorService = idGeneratorService;
	}

	public void logMotorcycleAction(Motorcycle motorcycle, String action, String performedByUserId,
			String performedByUsername) {
		MotorcycleLog log = new MotorcycleLog();
		log.setId(idGeneratorService.generateMotorcycleLogId());
		log.setMotorcycleId(motorcycle.getId());
		log.setBrand(motorcycle.getBrand());
		log.setModel(motorcycle.getModel());
		log.setAction(action);
		log.setTimestamp(Instant.now());
		log.setPerformedByUserId(performedByUserId);
		log.setPerformedByUsername(performedByUsername);
		motorcycleLogRepository.save(log);
	}

	public List<MotorcycleLog> getAllLogs(int pageSize, Map<String, AttributeValue> startKey,
			Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return motorcycleLogRepository.scanAll(pageSize, startKey, new ArrayList<>(), lastKeyHolder);
	}

	public List<MotorcycleLog> getFilteredLogs(Map<String, String> filters, int pageSize,
			Map<String, AttributeValue> startKey, Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return motorcycleLogRepository.queryWithFilters(filters, startKey, pageSize, lastKeyHolder);
	}
}
