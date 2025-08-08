package com.example.demo.service.log_service;

import com.example.demo.dto.log_dto.MotorcycleLogFilterRequestDTO;
import com.example.demo.model.Motorcycle;
import com.example.demo.model.log_model.MotorcycleLog;
import com.example.demo.repository.log_repository.MotorcycleLogRepository;
import com.example.demo.repository.log_repository.MotorcycleLogSpecifications;
import com.example.demo.utils.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class MotorcycleLogService {

	private final MotorcycleLogRepository motorcycleLogRepository;
	private final IdGeneratorService idGeneratorService;

	@Autowired
	public MotorcycleLogService(MotorcycleLogRepository motorcycleLogRepository, IdGeneratorService idGeneratorService) {
		this.motorcycleLogRepository = motorcycleLogRepository;
		this.idGeneratorService = idGeneratorService;
	}

	public void logMotorcycleAction(Motorcycle motorcycle, String action, String performedByUserId, String performedByUsername) {
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

	public Page<MotorcycleLog> getAllLogs(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
		return motorcycleLogRepository.findAll(pageable);
	}

	public Page<MotorcycleLog> getFilteredLogs(MotorcycleLogFilterRequestDTO filterRequest, int page, int size, String sortBy, String sortDir) {
		Specification<MotorcycleLog> spec = MotorcycleLogSpecifications.build(filterRequest);
		Sort sort = sortDir.equalsIgnoreCase("asc") ?
			Sort.by(sortBy).ascending() :
			Sort.by(sortBy).descending();
		Pageable pageable = PageRequest.of(page, size, sort);
		return motorcycleLogRepository.findAll(spec, pageable);
	}
}
