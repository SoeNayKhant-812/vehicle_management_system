package com.example.demo.service;

import com.example.demo.dao.TruckDAO;
import com.example.demo.dto.TruckDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Motorcycle;
import com.example.demo.model.Truck;
import com.example.demo.repository.TruckRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckService {

    @Autowired
    private TruckRepository truckRepository;
//    private TruckDAO truckDAO;

    public List<Truck> getAllTrucks() {
        return truckRepository.findAll();
    }

    public Truck getTruckById(Long id) {
        return truckRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Truck not found with ID: " + id));
    }

    public Truck addTruck(TruckDTO dto) {
        Truck truck = new Truck(null, dto.getBrand(), dto.getModel());
        return truckRepository.save(truck);
    }

    public Truck updateTruck(Long id, TruckDTO dto) {
        Truck existingTruck = truckRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Truck with ID " + id + " not found"));

        existingTruck.setBrand(dto.getBrand());
        existingTruck.setModel(dto.getModel());

        return truckRepository.save(existingTruck); // save works for both insert & update
    }

    public void deleteTruck(Long id) {
        if (!truckRepository.existsById(id)) {
            throw new VehicleNotFoundException("Cannot delete. Truck not found with ID: " + id);
        }
        truckRepository.deleteById(id);
    }
}
