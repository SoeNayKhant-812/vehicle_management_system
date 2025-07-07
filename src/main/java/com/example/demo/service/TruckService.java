package com.example.demo.service;

import com.example.demo.dto.TruckDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Truck;
import com.example.demo.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.logging.Logger;

@Service
public class TruckService {

	private static final Logger logger = Logger.getLogger(TruckService.class.getName());

	@Autowired
	private TruckRepository truckRepository;

	@Autowired
	private VehicleIdGeneratorService idGenerator;

	public List<Truck> getAllTrucks() {
		return truckRepository.findAll();
	}

	public Truck getTruckById(String id) {
		return truckRepository.findById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Truck not found with ID: " + id));
	}

	public Truck addTruck(TruckDTO dto) {
		Truck truck = new Truck();
		String generatedId = idGenerator.generateTruckId();
		truck.setId(generatedId);
		truck.setBrand(dto.getBrand());
		truck.setModel(dto.getModel());
		truck.setCreatedAt(Instant.now());

		logger.info("Generated new Truck ID: " + generatedId);

		return truckRepository.save(truck);
	}

	public Truck updateTruck(String id, TruckDTO dto) {
		Truck existingTruck = truckRepository.findById(id)
				.orElseThrow(() -> new VehicleNotFoundException("Truck with ID " + id + " not found"));

		existingTruck.setBrand(dto.getBrand());
		existingTruck.setModel(dto.getModel());

		return truckRepository.save(existingTruck);
	}

	public void deleteTruck(String id) {
		if (!truckRepository.existsById(id)) {
			throw new VehicleNotFoundException("Cannot delete. Truck not found with ID: " + id);
		}
		truckRepository.deleteById(id);
	}
}
