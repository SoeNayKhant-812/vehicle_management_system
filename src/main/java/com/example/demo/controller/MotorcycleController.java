package com.example.demo.controller;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.model.Motorcycle;
import com.example.demo.service.MotorcycleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/motorcycles")
public class MotorcycleController {

    @Autowired
    private MotorcycleService motorcycleService;

    @GetMapping
    public ResponseEntity<List<Motorcycle>> getAllMotorcycles() {
        return ResponseEntity.ok(motorcycleService.getAllMotorcycles());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Motorcycle> getMotorcycleById(@PathVariable Long id) {
        return ResponseEntity.ok(motorcycleService.getMotorcycleById(id));
    }

    @PostMapping("/addMotorcycle")
    public ResponseEntity<Motorcycle> addMotorcycle(@RequestBody MotorcycleDTO dto) {
        return ResponseEntity.ok(motorcycleService.addMotorcycle(dto));
    }

    @PutMapping("/{id}/update")
    public ResponseEntity<Motorcycle> updateMotorcycle(@PathVariable Long id, @RequestBody MotorcycleDTO dto) {
        Motorcycle updatedMotorcycle = motorcycleService.updateMotorcycle(id, dto);
        return ResponseEntity.ok(updatedMotorcycle);
    }

    @DeleteMapping("/{id}/delete")
    public ResponseEntity<String> deleteMotorcycle(@PathVariable Long id) {
        motorcycleService.deleteMotorcycle(id);
        return ResponseEntity.ok("Motorcycle deleted successfully");
    }
}
