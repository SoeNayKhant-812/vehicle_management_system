package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.CarLogDTO;
import com.example.demo.dto.log_dto.CarLogFilterRequestDTO;
import com.example.demo.model.log_model.CarLog;
import com.example.demo.repository.log_repository.CarLogRepository.Holder;
import com.example.demo.service.log_service.CarLogService;
import com.example.demo.utils.DynamoDbPaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

@RestController
@RequestMapping("/logs/car")
@PreAuthorize("hasRole('ADMIN')")
public class CarLogController {

	private final CarLogService carLogService;

	@Autowired
	public CarLogController(CarLogService carLogService) {
		this.carLogService = carLogService;
	}

	@GetMapping("/all")
	public ResponseEntity<CarLogDTO> getAllLogs(
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<CarLog> logs = carLogService.getAllLogs(size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new CarLogDTO(logs, lastKeyHolder.value));
	}

	@GetMapping("/filter")
	public ResponseEntity<CarLogDTO> getFilteredLogs(
			CarLogFilterRequestDTO filterRequest,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, String> filters = new HashMap<>();
		if (filterRequest.getId() != null) filters.put("id", filterRequest.getId());
		if (filterRequest.getCarId() != null) filters.put("carId", filterRequest.getCarId());
		if (filterRequest.getBrand() != null) filters.put("brand", filterRequest.getBrand());
		if (filterRequest.getModel() != null) filters.put("model", filterRequest.getModel());
		if (filterRequest.getAction() != null) filters.put("action", filterRequest.getAction());
		if (filterRequest.getPerformedByUserId() != null) filters.put("performedByUserId", filterRequest.getPerformedByUserId());
		if (filterRequest.getPerformedByUsername() != null) filters.put("performedByUsername", filterRequest.getPerformedByUsername());
		// Note: Timestamp range can be added as well in future using between expressions

		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<CarLog> logs = carLogService.getFilteredLogs(filters, size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new CarLogDTO(logs, lastKeyHolder.value));
	}
}
