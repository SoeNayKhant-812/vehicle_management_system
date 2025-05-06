package com.example.demo.service;

import com.example.demo.dao.MotorcycleDAO;
import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Motorcycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MotorcycleService {

    @Autowired
    private MotorcycleDAO motorcycleDAO;

    public List<Motorcycle> getAllMotorcycles() {
        return motorcycleDAO.findAll();
    }

    public Motorcycle getMotorcycleById(Long id) {
        Motorcycle motorcycle = motorcycleDAO.findById(id);
        if (motorcycle == null) throw new VehicleNotFoundException("Motorcycle not found with ID: " + id);
        return motorcycle;
    }

    public Motorcycle addMotorcycle(MotorcycleDTO dto) {
        Motorcycle motorcycle = new Motorcycle(null, dto.getBrand(), dto.getModel());
        return motorcycleDAO.save(motorcycle);
    }

    public Motorcycle updateMotorcycle(Long id, MotorcycleDTO dto) {
        Motorcycle updatedMotorcycle = new Motorcycle();
        updatedMotorcycle.setModel(dto.getModel());
        updatedMotorcycle.setBrand(dto.getBrand());
        Motorcycle result = motorcycleDAO.update(id, updatedMotorcycle);
        if (result == null) {
            throw new VehicleNotFoundException("Motorcycle with ID " + id + " not found");
        }
        return result;
    }

    public void deleteMotorcycle(Long id) {
        if (motorcycleDAO.findById(id) == null) {
            throw new VehicleNotFoundException("Cannot delete. Motorcycle not found with ID: " + id);
        }
        motorcycleDAO.delete(id);
    }
}
