package com.example.demo.dao;

import com.example.demo.model.Car;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class CarDAO {
    private final Map<Long, Car> carDB = new HashMap<>();
    private long idCounter = 1;

    public List<Car> findAll() {
        return new ArrayList<>(carDB.values());
    }

    public Car findById(Long id) {
        return carDB.get(id);
    }

    public Car save(Car car) {
        car.setId(idCounter++);
        carDB.put(car.getId(), car);
        return car;
    }

    public Car update(Long id, Car updatedCar) {
        if (carDB.containsKey(id)) {
            updatedCar.setId(id);
            carDB.put(id, updatedCar);
            return updatedCar;
        }
        return null;
    }

    public void delete(Long id) {
        carDB.remove(id);
    }
}
