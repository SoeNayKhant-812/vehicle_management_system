package com.example.demo.dto.log_dto;

import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.util.List;
import java.util.Map;

import com.example.demo.model.log_model.UserLog;

public class UserLogDTO {
	private List<UserLog> logs;
	private Map<String, AttributeValue> lastEvaluatedKey;

	public UserLogDTO(List<UserLog> logs, Map<String, AttributeValue> lastEvaluatedKey) {
		this.logs = logs;
		this.lastEvaluatedKey = lastEvaluatedKey;
	}

	public List<UserLog> getLogs() {
		return logs;
	}

	public void setLogs(List<UserLog> logs) {
		this.logs = logs;
	}

	public Map<String, AttributeValue> getLastEvaluatedKey() {
		return lastEvaluatedKey;
	}

	public void setLastEvaluatedKey(Map<String, AttributeValue> lastEvaluatedKey) {
		this.lastEvaluatedKey = lastEvaluatedKey;
	}
}