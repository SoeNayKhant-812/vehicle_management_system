package com.example.demo.service.log_service;

import com.example.demo.model.Truck;
import com.example.demo.model.log_model.TruckLog;
import com.example.demo.repository.log_repository.TruckLogRepository;
import com.example.demo.repository.log_repository.TruckLogRepository.Holder;
import com.example.demo.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.*;

@Service
public class TruckLogService {

	private final TruckLogRepository truckLogRepository;
	private final IdGeneratorService idGeneratorService;

	@Autowired
	public TruckLogService(TruckLogRepository truckLogRepository, IdGeneratorService idGeneratorService) {
		this.truckLogRepository = truckLogRepository;
		this.idGeneratorService = idGeneratorService;
	}

	public void logTruckAction(Truck truck, String action, String performedByUserId, String performedByUsername) {
		TruckLog log = buildTruckLog(truck, action, performedByUserId, performedByUsername);
		truckLogRepository.save(log);
	}

	public TruckLog buildTruckLog(Truck truck, String action, String performedByUserId, String performedByUsername) {
		TruckLog log = new TruckLog();
		log.setId(idGeneratorService.generateTruckLogId());
		log.setTruckId(truck.getId());
		log.setBrand(truck.getBrand());
		log.setModel(truck.getModel());
		log.setAction(action);
		log.setTimestamp(Instant.now());
		log.setPerformedByUserId(performedByUserId);
		log.setPerformedByUsername(performedByUsername);
		return log;
	}

	public List<TruckLog> getAllLogs(int pageSize, Map<String, AttributeValue> startKey,
			Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return truckLogRepository.scanAll(pageSize, startKey, new ArrayList<>(), lastKeyHolder);
	}

	public List<TruckLog> getFilteredLogs(Map<String, String> filters, int pageSize,
			Map<String, AttributeValue> startKey, Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return truckLogRepository.queryWithFilters(filters, startKey, pageSize, lastKeyHolder);
	}
}
