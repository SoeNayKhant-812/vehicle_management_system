package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class VehicleIdGeneratorService {

	public String generateCarId() {
		return "CAR_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateMotorcycleId() {
		return "MOTORCYCLE_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}

	public String generateTruckId() {
		return "TRUCK_" + UUID.randomUUID() + "_" + System.currentTimeMillis();
	}
}
