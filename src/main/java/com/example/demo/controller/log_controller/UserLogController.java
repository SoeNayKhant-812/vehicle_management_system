package com.example.demo.controller.log_controller;

import com.example.demo.dto.log_dto.UserLogDTO;
import com.example.demo.dto.log_dto.UserLogFilterRequestDTO;
import com.example.demo.model.log_model.UserLog;
import com.example.demo.repository.log_repository.UserLogRepository.Holder;
import com.example.demo.service.log_service.UserLogService;
import com.example.demo.utils.DynamoDbPaginationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.*;

@RestController
@RequestMapping("/logs/user")
@PreAuthorize("hasRole('ADMIN')")
public class UserLogController {

	private final UserLogService userLogService;

	@Autowired
	public UserLogController(UserLogService userLogService) {
		this.userLogService = userLogService;
	}

	@GetMapping("/all")
	public ResponseEntity<UserLogDTO> getAllLogs(
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<UserLog> logs = userLogService.getAllLogs(size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new UserLogDTO(logs, lastKeyHolder.value));
	}

	@GetMapping("/filter")
	public ResponseEntity<UserLogDTO> getFilteredLogs(
			UserLogFilterRequestDTO filterRequest,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Map<String, String> exclusiveStartKeyEncoded
	) {
		Map<String, String> filters = new HashMap<>();
		if (filterRequest.getId() != null) filters.put("id", filterRequest.getId());
		if (filterRequest.getUserId() != null) filters.put("userId", filterRequest.getUserId());
		if (filterRequest.getUsername() != null) filters.put("username", filterRequest.getUsername());
		if (filterRequest.getEmail() != null) filters.put("email", filterRequest.getEmail());
		if (filterRequest.getRole() != null) filters.put("role", filterRequest.getRole());
		if (filterRequest.getAction() != null) filters.put("action", filterRequest.getAction());
		if (filterRequest.getPerformedByUserId() != null) filters.put("performedByUserId", filterRequest.getPerformedByUserId());
		if (filterRequest.getPerformedByUsername() != null) filters.put("performedByUsername", filterRequest.getPerformedByUsername());
		// Timestamp range can be handled separately if needed

		Map<String, AttributeValue> startKey = DynamoDbPaginationUtil.decodeStartKey(exclusiveStartKeyEncoded);
		Holder<Map<String, AttributeValue>> lastKeyHolder = new Holder<>();

		List<UserLog> logs = userLogService.getFilteredLogs(filters, size, startKey, lastKeyHolder);
		return ResponseEntity.ok(new UserLogDTO(logs, lastKeyHolder.value));
	}
}
