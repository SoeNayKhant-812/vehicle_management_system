package com.example.demo.dao;

import com.example.demo.model.Motorcycle;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class MotorcycleDAO {
    private final Map<Long, Motorcycle> motorcycleDB = new HashMap<>();
    private long idCounter = 1;

    public List<Motorcycle> findAll() {
        return new ArrayList<>(motorcycleDB.values());
    }

    public Motorcycle findById(Long id) {
        return motorcycleDB.get(id);
    }

    public Motorcycle save(Motorcycle motorcycle) {
        motorcycle.setId(idCounter++);
        motorcycleDB.put(motorcycle.getId(), motorcycle);
        return motorcycle;
    }

    public Motorcycle update(Long id, Motorcycle updatedMotorcycle) {
        if (motorcycleDB.containsKey(id)) {
            updatedMotorcycle.setId(id);
            motorcycleDB.put(id, updatedMotorcycle);
            return updatedMotorcycle;
        }
        return null;
    }

    public void delete(Long id) {
        motorcycleDB.remove(id);
    }
}
