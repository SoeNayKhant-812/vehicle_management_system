package com.example.demo.controller;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.mapper.MotorcycleMapper;
import com.example.demo.model.Motorcycle;
import com.example.demo.service.MotorcycleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/motorcycles")
public class MotorcycleController {

	@Autowired
	private MotorcycleService motorcycleService;

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping
	public ResponseEntity<List<MotorcycleDTO>> getAllMotorcycles() {
		List<Motorcycle> motorcycles = motorcycleService.getAllMotorcycles();
		List<MotorcycleDTO> dtos = motorcycles.stream().map(MotorcycleMapper::toDTO).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<MotorcycleDTO> getMotorcycleById(@PathVariable String id) {
		Motorcycle motorcycle = motorcycleService.getMotorcycleById(id);
		return ResponseEntity.ok(MotorcycleMapper.toDTO(motorcycle));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PostMapping("/addMotorcycle")
	public ResponseEntity<MotorcycleDTO> addMotorcycle(@Valid @RequestBody MotorcycleDTO dto) {
		Motorcycle motorcycle = motorcycleService.addMotorcycle(dto);
		return ResponseEntity.ok(MotorcycleMapper.toDTO(motorcycle));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PutMapping("/{id}/update")
	public ResponseEntity<MotorcycleDTO> updateMotorcycle(@PathVariable String id,
			@Valid @RequestBody MotorcycleDTO dto) {
		Motorcycle motorcycle = motorcycleService.updateMotorcycle(id, dto);
		return ResponseEntity.ok(MotorcycleMapper.toDTO(motorcycle));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteMotorcycle(@PathVariable String id) {
		motorcycleService.deleteMotorcycle(id);
		return ResponseEntity.ok("Motorcycle deleted successfully");
	}
}
