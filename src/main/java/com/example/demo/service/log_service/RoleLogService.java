package com.example.demo.service.log_service;

import com.example.demo.model.Role;
import com.example.demo.model.log_model.RoleLog;
import com.example.demo.repository.log_repository.RoleLogRepository;
import com.example.demo.repository.log_repository.RoleLogRepository.Holder;
import com.example.demo.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.*;

@Service
public class RoleLogService {

	private final RoleLogRepository roleLogRepository;
	private final IdGeneratorService idGeneratorService;

	@Autowired
	public RoleLogService(RoleLogRepository roleLogRepository, IdGeneratorService idGeneratorService) {
		this.roleLogRepository = roleLogRepository;
		this.idGeneratorService = idGeneratorService;
	}

	public void logRoleAction(Role role, String action, String performedByUserId, String performedByUsername) {
		RoleLog log = buildRoleLog(role, action, performedByUserId, performedByUsername);
		roleLogRepository.save(log);
	}

	public RoleLog buildRoleLog(Role role, String action, String performedByUserId, String performedByUsername) {
		RoleLog log = new RoleLog();
		log.setId(idGeneratorService.generateRoleLogId());
		log.setRoleName(role.getName());
		log.setAction(action);
		log.setTimestamp(Instant.now());
		log.setPerformedByUserId(performedByUserId);
		log.setPerformedByUsername(performedByUsername);
		return log;
	}

	public List<RoleLog> getAllLogs(int pageSize, Map<String, AttributeValue> startKey,
			Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return roleLogRepository.scanAll(pageSize, startKey, new ArrayList<>(), lastKeyHolder);
	}

	public List<RoleLog> getFilteredLogs(Map<String, String> filters, int pageSize,
			Map<String, AttributeValue> startKey, Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return roleLogRepository.queryWithFilters(filters, startKey, pageSize, lastKeyHolder);
	}
}
