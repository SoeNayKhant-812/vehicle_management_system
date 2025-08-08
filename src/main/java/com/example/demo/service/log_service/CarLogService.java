package com.example.demo.service.log_service;

import com.example.demo.dto.log_dto.CarLogFilterRequestDTO;
import com.example.demo.model.Car;
import com.example.demo.model.log_model.CarLog;
import com.example.demo.repository.log_repository.CarLogRepository;
import com.example.demo.repository.log_repository.CarLogSpecifications;
import com.example.demo.utils.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class CarLogService {

	private final CarLogRepository carLogRepository;
	private final IdGeneratorService idGeneratorService;

	@Autowired
	public CarLogService(CarLogRepository carLogRepository, IdGeneratorService idGeneratorService) {
		this.carLogRepository = carLogRepository;
		this.idGeneratorService = idGeneratorService;
	}

	public void logCarAction(Car car, String action, String performedByUserId, String performedByUsername) {
		CarLog log = new CarLog();
		log.setId(idGeneratorService.generateCarLogId());
		log.setCarId(car.getId());
		log.setBrand(car.getBrand());
		log.setModel(car.getModel());
		log.setAction(action);
		log.setTimestamp(Instant.now());
		log.setPerformedByUserId(performedByUserId);
		log.setPerformedByUsername(performedByUsername);
		carLogRepository.save(log);
	}

	public Page<CarLog> getAllLogs(int page, int size) {
		Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "timestamp"));
		return carLogRepository.findAll(pageable);
	}

	public Page<CarLog> getFilteredLogs(CarLogFilterRequestDTO filterRequest, int page, int size, String sortBy, String sortDir) {
	    Specification<CarLog> spec = CarLogSpecifications.build(filterRequest);
	    Sort sort = sortDir.equalsIgnoreCase("asc") ?
	        Sort.by(sortBy).ascending() :
	        Sort.by(sortBy).descending();
	    Pageable pageable = PageRequest.of(page, size, sort);
	    return carLogRepository.findAll(spec, pageable);
	}

}
