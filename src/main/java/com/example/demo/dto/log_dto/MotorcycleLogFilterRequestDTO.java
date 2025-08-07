package com.example.demo.dto.log_dto;

import java.time.Instant;

public class MotorcycleLogFilterRequestDTO {
	private String id;
	private String motorcycleId;
	private String brand;
	private String model;
	private String action;
	private String performedByUserId;
	private String performedByUsername;
	private Instant startTime;
	private Instant endTime;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getMotorcycleId() {
		return motorcycleId;
	}

	public void setMotorcycleId(String motorcycleId) {
		this.motorcycleId = motorcycleId;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getPerformedByUserId() {
		return performedByUserId;
	}

	public void setPerformedByUserId(String performedByUserId) {
		this.performedByUserId = performedByUserId;
	}

	public String getPerformedByUsername() {
		return performedByUsername;
	}

	public void setPerformedByUsername(String performedByUsername) {
		this.performedByUsername = performedByUsername;
	}

	public Instant getStartTime() {
		return startTime;
	}

	public void setStartTime(Instant startTime) {
		this.startTime = startTime;
	}

	public Instant getEndTime() {
		return endTime;
	}

	public void setEndTime(Instant endTime) {
		this.endTime = endTime;
	}
}
