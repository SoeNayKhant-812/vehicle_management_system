package com.example.demo.utils;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class IdGeneratorService {

	// IDs for main entities
	public String generateCarId() {
		return "CAR_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateMotorcycleId() {
		return "MOTORCYCLE_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateTruckId() {
		return "TRUCK_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateUserId() {
		return "USER_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	// IDs for log entities
	public String generateCarLogId() {
		return "CAR_LOG_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateMotorcycleLogId() {
		return "MOTORCYCLE_LOG_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateTruckLogId() {
		return "TRUCK_LOG_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateUserLogId() {
		return "USER_LOG_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateRoleLogId() {
		return "ROLE_LOG_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}
}