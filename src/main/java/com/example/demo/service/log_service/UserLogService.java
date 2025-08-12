package com.example.demo.service.log_service;

import com.example.demo.model.User;
import com.example.demo.model.log_model.UserLog;
import com.example.demo.repository.log_repository.UserLogRepository;
import com.example.demo.repository.log_repository.UserLogRepository.Holder;
import com.example.demo.service.IdGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.Instant;
import java.util.*;

@Service
public class UserLogService {

	private final UserLogRepository userLogRepository;
	private final IdGeneratorService idGeneratorService;

	@Autowired
	public UserLogService(UserLogRepository userLogRepository, IdGeneratorService idGeneratorService) {
		this.userLogRepository = userLogRepository;
		this.idGeneratorService = idGeneratorService;
	}

	public void logUserAction(User user, String action, String performedByUserId, String performedByUsername) {
		UserLog log = buildUserLog(user, action, performedByUserId, performedByUsername);
		userLogRepository.save(log);
	}

	public UserLog buildUserLog(User user, String action, String performedByUserId, String performedByUsername) {
		UserLog log = new UserLog();
		log.setId(idGeneratorService.generateUserLogId());
		log.setUserId(user.getId());
		log.setUsername(user.getUsername());
		log.setEmail(user.getEmail());
		log.setRole(user.getRole().getName());
		log.setAction(action);
		log.setTimestamp(Instant.now());
		log.setPerformedByUserId(performedByUserId);
		log.setPerformedByUsername(performedByUsername);
		return log;
	}

	public List<UserLog> getAllLogs(int pageSize, Map<String, AttributeValue> startKey,
			Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return userLogRepository.scanAll(pageSize, startKey, new ArrayList<>(), lastKeyHolder);
	}

	public List<UserLog> getFilteredLogs(Map<String, String> filters, int pageSize,
			Map<String, AttributeValue> startKey, Holder<Map<String, AttributeValue>> lastKeyHolder) {
		return userLogRepository.queryWithFilters(filters, startKey, pageSize, lastKeyHolder);
	}
}
