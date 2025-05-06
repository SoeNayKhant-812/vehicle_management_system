package com.example.demo.dao;

import com.example.demo.model.Truck;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class TruckDAO {
    private final Map<Long, Truck> truckDB = new HashMap<>();
    private long idCounter = 1;

    public List<Truck> findAll() {
        return new ArrayList<>(truckDB.values());
    }

    public Truck findById(Long id) {
        return truckDB.get(id);
    }

    public Truck save(Truck truck) {
        truck.setId(idCounter++);
        truckDB.put(truck.getId(), truck);
        return truck;
    }

    public Truck update(Long id, Truck updatedTruck) {
        if (truckDB.containsKey(id)) {
            updatedTruck.setId(id);
            truckDB.put(id, updatedTruck);
            return updatedTruck;
        }
        return null;
    }

    public void delete(Long id) {
        truckDB.remove(id);
    }
}
