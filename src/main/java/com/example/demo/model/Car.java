package com.example.demo.model;

import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.*;

import java.io.Serializable;
import java.time.Instant;

@DynamoDbBean
public class Car implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String brand;
	private String model;
	private Instant createdAt;

	@DynamoDbPartitionKey
	@DynamoDbAttribute("id")
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	@DynamoDbAttribute("createdAt")
	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}
}
