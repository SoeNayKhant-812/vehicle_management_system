package com.example.demo.service;

import com.example.demo.dao.TruckDAO;
import com.example.demo.dto.TruckDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Truck;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TruckService {

    @Autowired
    private TruckDAO truckDAO;

    public List<Truck> getAllTrucks() {
        return truckDAO.findAll();
    }

    public Truck getTruckById(Long id) {
        Truck truck = truckDAO.findById(id);
        if (truck == null) throw new VehicleNotFoundException("Truck not found with ID: " + id);
        return truck;
    }

    public Truck addTruck(TruckDTO dto) {
        Truck truck = new Truck(null, dto.getBrand(), dto.getModel());
        return truckDAO.save(truck);
    }

    public Truck updateTruck(Long id, TruckDTO dto) {
        Truck updatedTruck = new Truck();
        updatedTruck.setModel(dto.getModel());
        updatedTruck.setBrand(dto.getBrand());
        Truck result = truckDAO.update(id, updatedTruck);
        if (result == null) {
            throw new VehicleNotFoundException("Truck with ID " + id + " not found");
        }
        return result;
    }

    public void deleteTruck(Long id) {
        if (truckDAO.findById(id) == null) {
            throw new VehicleNotFoundException("Cannot delete. Truck not found with ID: " + id);
        }
        truckDAO.delete(id);
    }
}
