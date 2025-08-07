package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.MotorcycleLogDTO;
import com.example.demo.dto.log_dto.MotorcycleLogFilterRequestDTO;
import com.example.demo.model.log_model.MotorcycleLog;
import com.example.demo.repository.log_repository.MotorcycleLogRepository.Holder;
import com.example.demo.service.log_service.MotorcycleLogService;
import com.example.demo.utils.DynamoDbPaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

@RestController
@RequestMapping("/logs/motorcycle")
@PreAuthorize("hasRole('ADMIN')")
public class MotorcycleLogController {

	private final MotorcycleLogService motorcycleLogService;

	@Autowired
	public MotorcycleLogController(MotorcycleLogService motorcycleLogService) {
		this.motorcycleLogService = motorcycleLogService;
	}

	@GetMapping("/all")
	public ResponseEntity<MotorcycleLogDTO> getAllLogs(
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<MotorcycleLog> logs = motorcycleLogService.getAllLogs(size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new MotorcycleLogDTO(logs, lastKeyHolder.value));
	}

	@GetMapping("/filter")
	public ResponseEntity<MotorcycleLogDTO> getFilteredLogs(
			MotorcycleLogFilterRequestDTO filterRequest,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, String> filters = new HashMap<>();
		if (filterRequest.getId() != null) filters.put("id", filterRequest.getId());
		if (filterRequest.getMotorcycleId() != null) filters.put("motorcycleId", filterRequest.getMotorcycleId());
		if (filterRequest.getBrand() != null) filters.put("brand", filterRequest.getBrand());
		if (filterRequest.getModel() != null) filters.put("model", filterRequest.getModel());
		if (filterRequest.getAction() != null) filters.put("action", filterRequest.getAction());
		if (filterRequest.getPerformedByUserId() != null) filters.put("performedByUserId", filterRequest.getPerformedByUserId());
		if (filterRequest.getPerformedByUsername() != null) filters.put("performedByUsername", filterRequest.getPerformedByUsername());
		// You can also add timestamp range if needed

		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<MotorcycleLog> logs = motorcycleLogService.getFilteredLogs(filters, size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new MotorcycleLogDTO(logs, lastKeyHolder.value));
	}
}
