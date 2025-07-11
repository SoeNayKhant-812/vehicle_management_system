package com.example.demo.controller;

import com.example.demo.dto.TruckDTO;
import com.example.demo.model.Truck;
import com.example.demo.service.TruckService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trucks")
public class TruckController {

	@Autowired
	private TruckService truckService;

	@GetMapping
	public ResponseEntity<List<Truck>> getAllTrucks() {
		return ResponseEntity.ok(truckService.getAllTrucks());
	}

	@GetMapping("/{id}")
	public ResponseEntity<Truck> getTruckById(@PathVariable String id) {
		return ResponseEntity.ok(truckService.getTruckById(id));
	}

	@PostMapping("/addTruck")
	public ResponseEntity<Truck> addTruck(@RequestBody TruckDTO dto) {
		return ResponseEntity.ok(truckService.addTruck(dto));
	}

	@PutMapping("/{id}/update")
	public ResponseEntity<Truck> updateTruck(@PathVariable String id, @RequestBody TruckDTO dto) {
		Truck updatedTruck = truckService.updateTruck(id, dto);
		return ResponseEntity.ok(updatedTruck);
	}

	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteTruck(@PathVariable String id) {
		truckService.deleteTruck(id);
		return ResponseEntity.ok("Truck deleted successfully");
	}
}
