package com.example.demo.controller;

import com.example.demo.dto.TruckDTO;
import com.example.demo.mapper.TruckMapper;
import com.example.demo.model.Truck;
import com.example.demo.service.TruckService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/trucks")
public class TruckController {

	@Autowired
	private TruckService truckService;

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping
	public ResponseEntity<List<TruckDTO>> getAllTrucks() {
		List<Truck> trucks = truckService.getAllTrucks();
		List<TruckDTO> dtos = trucks.stream().map(TruckMapper::toDTO).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<TruckDTO> getTruckById(@PathVariable String id) {
		Truck truck = truckService.getTruckById(id);
		return ResponseEntity.ok(TruckMapper.toDTO(truck));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PostMapping("/addTruck")
	public ResponseEntity<TruckDTO> addTruck(@Valid @RequestBody TruckDTO dto) {
		Truck truck = truckService.addTruck(dto);
		return ResponseEntity.ok(TruckMapper.toDTO(truck));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PutMapping("/{id}/update")
	public ResponseEntity<TruckDTO> updateTruck(@PathVariable String id, @Valid @RequestBody TruckDTO dto) {
		Truck truck = truckService.updateTruck(id, dto);
		return ResponseEntity.ok(TruckMapper.toDTO(truck));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteTruck(@PathVariable String id) {
		truckService.deleteTruck(id);
		return ResponseEntity.ok("Truck deleted successfully");
	}
}
