package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.mapper.CarMapper;
import com.example.demo.model.Car;
import com.example.demo.service.CarService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cars")
public class CarController {

	@Autowired
	private CarService carService;

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping
	public ResponseEntity<List<CarDTO>> getAllCars() {
		List<Car> cars = carService.getAllCars();
		List<CarDTO> dtos = cars.stream().map(CarMapper::toDTO).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<CarDTO> getCarById(@PathVariable String id) {
		Car car = carService.getCarById(id);
		return ResponseEntity.ok(CarMapper.toDTO(car));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PostMapping("/addCar")
	public ResponseEntity<CarDTO> addCar(@Valid @RequestBody CarDTO dto) {
		Car car = carService.addCar(dto);
		return ResponseEntity.ok(CarMapper.toDTO(car));
	}

	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PutMapping("/{id}/update")
	public ResponseEntity<CarDTO> updateCar(@PathVariable String id, @Valid @RequestBody CarDTO dto) {
		Car car = carService.updateCar(id, dto);
		return ResponseEntity.ok(CarMapper.toDTO(car));
	}

	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteCar(@PathVariable String id) {
		carService.deleteCar(id);
		return ResponseEntity.ok("Car deleted successfully");
	}
}
