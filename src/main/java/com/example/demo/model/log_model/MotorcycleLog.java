package com.example.demo.model.log_model;

import java.time.Instant;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondaryPartitionKey;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbSecondarySortKey;

@DynamoDbBean
public class MotorcycleLog {

	// Constant for GSI name to avoid magic strings
	public static final String MOTORCYCLE_ID_TIMESTAMP_INDEX = "motorcycleId-timestamp-index";

	private String id;
	private String motorcycleId;
	private String brand;
	private String model;
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

	@DynamoDbSecondaryPartitionKey(indexNames = MOTORCYCLE_ID_TIMESTAMP_INDEX)
	@DynamoDbAttribute("motorcycleId")
	public String getMotorcycleId() {
		return motorcycleId;
	}

	public void setMotorcycleId(String motorcycleId) {
		this.motorcycleId = motorcycleId;
	}

	@DynamoDbAttribute("brand")
	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	@DynamoDbAttribute("model")
	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	@DynamoDbAttribute("action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@DynamoDbSecondarySortKey(indexNames = MOTORCYCLE_ID_TIMESTAMP_INDEX)
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
