package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.mapper.CarMapper;
import com.example.demo.model.Car;
import com.example.demo.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cars")
@Tag(name = "Car Management")
public class CarController {

	@Autowired
	private CarService carService;

	@Operation(
			summary = "Retrieve a list of all cars",
			description = "Fetches all car entries from the system. The result is cached for performance."
	)
	@ApiResponse(
			responseCode = "200",
			description = "Successfully retrieved the list of cars.",
			content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CarDTO[].class))
	)
	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping
	public ResponseEntity<List<CarDTO>> getAllCars() {
		List<Car> cars = carService.getAllCars();
		List<CarDTO> dtos = cars.stream().map(CarMapper::toDTO).collect(Collectors.toList());
		return ResponseEntity.ok(dtos);
	}

	@Operation(
			summary = "Retrieve a car by its ID",
			description = "Fetches the details of a specific car using its unique identifier."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Successfully retrieved the car.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CarDTO.class))),
			@ApiResponse(responseCode = "404", description = "A car with the specified ID was not found.", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to access this resource.", content = @Content)
	})
	@PreAuthorize("hasAnyRole('USER', 'STAFF', 'ADMIN')")
	@GetMapping("/{id}")
	public ResponseEntity<CarDTO> getCarById(
			@Parameter(description = "The unique ID of the car.", required = true, example = "car-12345")
			@PathVariable String id) {
		Car car = carService.getCarById(id);
		return ResponseEntity.ok(CarMapper.toDTO(car));
	}

	@Operation(
			summary = "Add a new car",
			description = "Creates a new car record in the system. The operation is transactional and includes an audit log entry."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "201", description = "Car created successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CarDTO.class))),
			@ApiResponse(responseCode = "400", description = "Invalid request body provided.", content = @Content)
	})
	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PostMapping("/addCar")
	public ResponseEntity<CarDTO> addCar(@Valid @RequestBody CarDTO dto) {
		Car newCar = carService.addCar(dto);
		return ResponseEntity.status(HttpStatus.CREATED).body(CarMapper.toDTO(newCar));
	}

	@Operation(
			summary = "Update an existing car",
			description = "Updates the details of an existing car identified by its unique ID."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Car updated successfully.", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE, schema = @Schema(implementation = CarDTO.class))),
			@ApiResponse(responseCode = "404", description = "A car with the specified ID was not found.", content = @Content),
			@ApiResponse(responseCode = "400", description = "Invalid request body provided.", content = @Content)
	})
	@PreAuthorize("hasAnyRole('STAFF', 'ADMIN')")
	@PutMapping("/{id}/update")
	public ResponseEntity<CarDTO> updateCar(
			@Parameter(description = "The ID of the car to update.", required = true) @PathVariable String id,
			@Valid @RequestBody CarDTO dto) {
		Car updatedCar = carService.updateCar(id, dto);
		return ResponseEntity.ok(CarMapper.toDTO(updatedCar));
	}

	@Operation(
			summary = "Delete a car",
			description = "Deletes a car from the system by its unique ID. This operation cannot be undone."
	)
	@ApiResponses({
			@ApiResponse(responseCode = "200", description = "Car deleted successfully.", content = @Content),
			@ApiResponse(responseCode = "404", description = "A car with the specified ID was not found.", content = @Content),
			@ApiResponse(responseCode = "403", description = "Forbidden - You do not have permission to perform this action.", content = @Content)
	})
	@PreAuthorize("hasRole('ADMIN')")
	@DeleteMapping("/{id}/delete")
	public ResponseEntity<String> deleteCar(
			@Parameter(description = "The ID of the car to delete.", required = true)
			@PathVariable String id) {
		carService.deleteCar(id);
		return ResponseEntity.ok("Car with ID '" + id + "' deleted successfully.");
	}
}