package com.example.demo.controller;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.model.Motorcycle;
import com.example.demo.service.MotorcycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/motorcycles")
public class MotorcycleController {

	@Autowired
	private MotorcycleService motorcycleService;

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping
	public ResponseEntity<List<Motorcycle>> getAllMotorcycles() {
		return ResponseEntity.ok(motorcycleService.getAllMotorcycles());
	}

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<Motorcycle> getMotorcycleById(@PathVariable String id) {
		return ResponseEntity.ok(motorcycleService.getMotorcycleById(id));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PostMapping("/addMotorcycle")
	public ResponseEntity<Motorcycle> addMotorcycle(@RequestBody MotorcycleDTO dto) {
		return ResponseEntity.ok(motorcycleService.addMotorcycle(dto));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PutMapping("/{id}/update")
	public ResponseEntity<Motorcycle> updateMotorcycle(@PathVariable String id, @RequestBody MotorcycleDTO dto) {
		return ResponseEntity.ok(motorcycleService.updateMotorcycle(id, dto));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteMotorcycle(@PathVariable String id) {
		motorcycleService.deleteMotorcycle(id);
		return ResponseEntity.ok("Motorcycle deleted successfully");
	}
}
