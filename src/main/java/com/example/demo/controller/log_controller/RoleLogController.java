package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.RoleLogDTO;
import com.example.demo.dto.log_dto.RoleLogFilterRequestDTO;
import com.example.demo.model.log_model.RoleLog;
import com.example.demo.repository.log_repository.RoleLogRepository.Holder;
import com.example.demo.service.log_service.RoleLogService;
import com.example.demo.utils.DynamoDbPaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

@RestController
@RequestMapping("/logs/role")
@PreAuthorize("hasRole('ADMIN')")
public class RoleLogController {

	private final RoleLogService roleLogService;

	@Autowired
	public RoleLogController(RoleLogService roleLogService) {
		this.roleLogService = roleLogService;
	}

	@GetMapping("/all")
	public ResponseEntity<RoleLogDTO> getAllLogs(
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<RoleLog> logs = roleLogService.getAllLogs(size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new RoleLogDTO(logs, lastKeyHolder.value));
	}

	@GetMapping("/filter")
	public ResponseEntity<RoleLogDTO> getFilteredLogs(
			RoleLogFilterRequestDTO filterRequest,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, String> filters = new HashMap<>();
		if (filterRequest.getId() != null) filters.put("id", filterRequest.getId());
		if (filterRequest.getRoleName() != null) filters.put("roleName", filterRequest.getRoleName());
		if (filterRequest.getAction() != null) filters.put("action", filterRequest.getAction());
		if (filterRequest.getPerformedByUserId() != null) filters.put("performedByUserId", filterRequest.getPerformedByUserId());
		if (filterRequest.getPerformedByUsername() != null) filters.put("performedByUsername", filterRequest.getPerformedByUsername());

		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<RoleLog> logs = roleLogService.getFilteredLogs(filters, size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new RoleLogDTO(logs, lastKeyHolder.value));
	}
}
