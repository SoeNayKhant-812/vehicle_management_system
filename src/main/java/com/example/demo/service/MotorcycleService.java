package com.example.demo.service;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Motorcycle;
import com.example.demo.repository.MotorcycleRepository;
import com.example.demo.service.log_service.MotorcycleLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class MotorcycleService {

    @Autowired
    private MotorcycleRepository motorcycleRepository;

    @Autowired
    private MotorcycleLogService motorcycleLogService;

    @Transactional(readOnly = true)
    public List<Motorcycle> getAllMotorcycles() {
        return motorcycleRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Motorcycle getMotorcycleById(Long id) {
        return motorcycleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Motorcycle not found with ID: " + id));
    }

    @Transactional
    public Motorcycle addMotorcycle(MotorcycleDTO dto) {
        Motorcycle motorcycle = new Motorcycle(null, dto.getBrand(), dto.getModel());
        Motorcycle savedMotorcycle = motorcycleRepository.save(motorcycle);
        
        motorcycleLogService.logMotorcycleAction(savedMotorcycle, "CREATE", "system-user-id", "system");

        return savedMotorcycle;
    }

    @Transactional
    public Motorcycle updateMotorcycle(Long id, MotorcycleDTO dto) {
        Motorcycle existingMotorcycle = motorcycleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Motorcycle with ID " + id + " not found"));

        existingMotorcycle.setBrand(dto.getBrand());
        existingMotorcycle.setModel(dto.getModel());
        Motorcycle updatedMotorcycle = motorcycleRepository.save(existingMotorcycle);

        motorcycleLogService.logMotorcycleAction(updatedMotorcycle, "UPDATE", "system-user-id", "system");

        return updatedMotorcycle;
    }

    @Transactional
    public void deleteMotorcycle(Long id) {
        Motorcycle motorcycleToDelete = motorcycleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Cannot delete. Motorcycle not found with ID: " + id));

        motorcycleRepository.deleteById(id);

        motorcycleLogService.logMotorcycleAction(motorcycleToDelete, "DELETE", "system-user-id", "system");
    }
}
