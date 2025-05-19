package com.example.demo.service;

import com.example.demo.dao.MotorcycleDAO;
import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.model.Motorcycle;
import com.example.demo.repository.MotorcycleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotorcycleService {

    @Autowired
    private MotorcycleRepository motorcycleRepository;
//    private MotorcycleDAO motorcycleDAO;

    public List<Motorcycle> getAllMotorcycles() {
        return motorcycleRepository.findAll();
    }

    public Motorcycle getMotorcycleById(Long id) {
        return motorcycleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Motorcycle not found with ID: " + id));
    }

    public Motorcycle addMotorcycle(MotorcycleDTO dto) {
        Motorcycle motorcycle = new Motorcycle(null, dto.getBrand(), dto.getModel());
        return motorcycleRepository.save(motorcycle);
    }

    public Motorcycle updateMotorcycle(Long id, MotorcycleDTO dto) {
        Motorcycle existingMotorcycle = motorcycleRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Motorcycle with ID " + id + " not found"));

        existingMotorcycle.setBrand(dto.getBrand());
        existingMotorcycle.setModel(dto.getModel());

        return motorcycleRepository.save(existingMotorcycle); // save works for both insert & update
    }

    public void deleteMotorcycle(Long id) {
        if (!motorcycleRepository.existsById(id)) {
            throw new VehicleNotFoundException("Cannot delete. Motorcycle not found with ID: " + id);
        }
        motorcycleRepository.deleteById(id);
    }
}
