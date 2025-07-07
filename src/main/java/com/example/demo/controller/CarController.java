package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.model.Car;
import com.example.demo.service.CarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarController {

    @Autowired
    private CarService carService;

    @GetMapping
    public ResponseEntity<List<Car>> getAllCars() {
        return ResponseEntity.ok(carService.getAllCars());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable String id) {
        return ResponseEntity.ok(carService.getCarById(id));
    }

    @PostMapping("/addCar")
    public ResponseEntity<Car> addCar(@RequestBody CarDTO dto) {
        return ResponseEntity.ok(carService.addCar(dto));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Car> updateCar(@PathVariable String id, @RequestBody CarDTO dto) {
        Car updatedCar = carService.updateCar(id, dto);
        return ResponseEntity.ok(updatedCar);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteCar(@PathVariable String id) {
        carService.deleteCar(id);
        return ResponseEntity.ok("Car deleted successfully");
    }
}
