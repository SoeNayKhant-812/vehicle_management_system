package com.example.demo.model.log_model;

import java.time.Instant;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class RoleLog {

	public static final String ROLE_NAME_TIMESTAMP_INDEX = "roleName-timestamp-index";

	private String id;
	private String roleName;
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

	@DynamoDbSecondaryPartitionKey(indexNames = ROLE_NAME_TIMESTAMP_INDEX)
	@DynamoDbAttribute("roleName")
	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	@DynamoDbAttribute("action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@DynamoDbSecondarySortKey(indexNames = ROLE_NAME_TIMESTAMP_INDEX)
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
