package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.TruckLogDTO;
import com.example.demo.dto.log_dto.TruckLogFilterRequestDTO;
import com.example.demo.model.log_model.TruckLog;
import com.example.demo.repository.log_repository.TruckLogRepository.Holder;
import com.example.demo.service.log_service.TruckLogService;
import com.example.demo.utils.DynamoDbPaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

@RestController
@RequestMapping("/logs/truck")
@PreAuthorize("hasRole('ADMIN')")
public class TruckLogController {

	private final TruckLogService truckLogService;

	@Autowired
	public TruckLogController(TruckLogService truckLogService) {
		this.truckLogService = truckLogService;
	}

	@GetMapping("/all")
	public ResponseEntity<TruckLogDTO> getAllLogs(
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<TruckLog> logs = truckLogService.getAllLogs(size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new TruckLogDTO(logs, lastKeyHolder.value));
	}

	@GetMapping("/filter")
	public ResponseEntity<TruckLogDTO> getFilteredLogs(
			TruckLogFilterRequestDTO filterRequest,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, String> filters = new HashMap<>();
		if (filterRequest.getId() != null) filters.put("id", filterRequest.getId());
		if (filterRequest.getTruckId() != null) filters.put("truckId", filterRequest.getTruckId());
		if (filterRequest.getBrand() != null) filters.put("brand", filterRequest.getBrand());
		if (filterRequest.getModel() != null) filters.put("model", filterRequest.getModel());
		if (filterRequest.getAction() != null) filters.put("action", filterRequest.getAction());
		if (filterRequest.getPerformedByUserId() != null) filters.put("performedByUserId", filterRequest.getPerformedByUserId());
		if (filterRequest.getPerformedByUsername() != null) filters.put("performedByUsername", filterRequest.getPerformedByUsername());
		// Timestamp filtering can be added later if needed

		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<TruckLog> logs = truckLogService.getFilteredLogs(filters, size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new TruckLogDTO(logs, lastKeyHolder.value));
	}
}
