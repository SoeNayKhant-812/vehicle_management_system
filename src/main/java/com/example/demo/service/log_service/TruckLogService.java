package com.example.demo.service.log_service;

import com.example.demo.dto.log_dto.TruckLogFilterRequestDTO;
import com.example.demo.model.Truck;
import com.example.demo.model.log_model.TruckLog;
import com.example.demo.repository.log_repository.TruckLogRepository;
import com.example.demo.repository.log_repository.TruckLogSpecifications;
import com.example.demo.utils.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;

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
		TruckLog log = new TruckLog();
		log.setId(idGeneratorService.generateTruckLogId());
		log.setTruckId(truck.getId());
		log.setBrand(truck.getBrand());
		log.setModel(truck.getModel());
		log.setAction(action);
		log.setTimestamp(Instant.now());
		log.setPerformedByUserId(performedByUserId);
		log.setPerformedByUsername(performedByUsername);
		truckLogRepository.save(log);
	}

	public Page<TruckLog> getAllLogs(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
		return truckLogRepository.findAll(pageable);
	}

	public Page<TruckLog> getFilteredLogs(TruckLogFilterRequestDTO filterRequest, int page, int size, String sortBy, String sortDir) {
		Specification<TruckLog> spec = TruckLogSpecifications.build(filterRequest);
		Sort sort = sortDir.equalsIgnoreCase("asc") ?
			Sort.by(sortBy).ascending() :
			Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return truckLogRepository.findAll(spec, pageable);
	}
}
