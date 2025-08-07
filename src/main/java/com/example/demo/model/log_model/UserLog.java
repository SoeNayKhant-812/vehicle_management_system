package com.example.demo.model.log_model;

import java.time.Instant;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class UserLog {

	public static final String USER_ID_TIMESTAMP_INDEX = "userId-timestamp-index";

	private String id;
	private String userId;
	private String username;
	private String email;
	private String role;
	private String action;
	private Instant timestamp;
	private String performedByUserId;
	private String performedByUsername;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	@DynamoDbSecondaryPartitionKey(indexNames = USER_ID_TIMESTAMP_INDEX)
	@DynamoDbAttribute("userId")
	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	@DynamoDbAttribute("username")
	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	@DynamoDbAttribute("email")
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@DynamoDbAttribute("role")
	public String getRole() {
		return role;
	}

	public void setRole(String role) {
		this.role = role;
	}

	@DynamoDbAttribute("action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@DynamoDbSecondarySortKey(indexNames = USER_ID_TIMESTAMP_INDEX)
	@DynamoDbAttribute("timestamp")
	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	@DynamoDbAttribute("performedByUserId")
	public String getPerformedByUserId() {
		return performedByUserId;
	}

	public void setPerformedByUserId(String performedByUserId) {
		this.performedByUserId = performedByUserId;
	}

	@DynamoDbAttribute("performedByUsername")
	public String getPerformedByUsername() {
		return performedByUsername;
	}

	public void setPerformedByUsername(String performedByUsername) {
		this.performedByUsername = performedByUsername;
	}
}
