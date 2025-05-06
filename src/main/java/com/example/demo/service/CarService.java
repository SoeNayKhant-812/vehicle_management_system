package com.example.demo.service;

import com.example.demo.dao.CarDAO;
import com.example.demo.dto.CarDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Car;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarDAO carDAO;

    public List<Car> getAllCars() {
        return carDAO.findAll();
    }

    public Car getCarById(Long id) {
        Car car = carDAO.findById(id);
        if (car == null) throw new VehicleNotFoundException("Car not found with ID: " + id);
        return car;
    }

    public Car addCar(CarDTO dto) {
        Car car = new Car(null, dto.getBrand(), dto.getModel());
        return carDAO.save(car);
    }

    public Car updateCar(Long id, CarDTO dto) {
        Car updatedCar = new Car();
        updatedCar.setModel(dto.getModel());
        updatedCar.setBrand(dto.getBrand());
        Car result = carDAO.update(id, updatedCar);
        if (result == null) {
            throw new VehicleNotFoundException("Car with ID " + id + " not found");
        }
        return result;
    }

    public void deleteCar(Long id) {
        if (carDAO.findById(id) == null) {
            throw new VehicleNotFoundException("Cannot delete. Car not found with ID: " + id);
        }
        carDAO.delete(id);
    }
}
