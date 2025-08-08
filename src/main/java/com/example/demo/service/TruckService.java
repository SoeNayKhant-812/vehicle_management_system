package com.example.demo.service;

import com.example.demo.dto.TruckDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Truck;
import com.example.demo.repository.TruckRepository;
import com.example.demo.service.log_service.TruckLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TruckService {

    @Autowired
    private TruckRepository truckRepository;

    @Autowired
    private TruckLogService truckLogService;

    @Transactional(readOnly = true)
    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Truck getTruckById(Long id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Truck not found with ID: " + id));
    }

    @Transactional
    public Truck addTruck(TruckDTO dto) {
        Truck truck = new Truck(null, dto.getBrand(), dto.getModel());
        Truck savedTruck = truckRepository.save(truck);

        truckLogService.logTruckAction(savedTruck, "CREATE", "system-user-id", "system");

        return savedTruck;
    }

    @Transactional
    public Truck updateTruck(Long id, TruckDTO dto) {
        Truck existingTruck = truckRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Truck with ID " + id + " not found"));

        existingTruck.setBrand(dto.getBrand());
        existingTruck.setModel(dto.getModel());
        Truck updatedTruck = truckRepository.save(existingTruck);

        truckLogService.logTruckAction(updatedTruck, "UPDATE", "system-user-id", "system");

        return updatedTruck;
    }

    @Transactional
    public void deleteTruck(Long id) {
        Truck truckToDelete = truckRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Cannot delete. Truck not found with ID: " + id));

        truckRepository.deleteById(id);

        truckLogService.logTruckAction(truckToDelete, "DELETE", "system-user-id", "system");
    }
}
