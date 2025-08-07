package com.example.demo.service.log_service;

import com.example.demo.model.Car;
import com.example.demo.model.log_model.CarLog;
import com.example.demo.repository.log_repository.CarLogRepository;
import com.example.demo.repository.log_repository.CarLogRepository.Holder;
import com.example.demo.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.*;

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

    public List<CarLog> getAllLogs(int pageSize, Map<String, AttributeValue> startKey, 
                                   Holder<Map<String, AttributeValue>> lastKeyHolder) {
        return carLogRepository.scanAll(pageSize, startKey, new ArrayList<>(), lastKeyHolder);
    }

    public List<CarLog> getFilteredLogs(Map<String, String> filters, int pageSize, 
                                        Map<String, AttributeValue> startKey,
                                        Holder<Map<String, AttributeValue>> lastKeyHolder) {
        return carLogRepository.queryWithFilters(filters, startKey, pageSize, lastKeyHolder);
    }
}