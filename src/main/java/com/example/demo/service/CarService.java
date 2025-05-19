package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Car not found with ID: " + id));
    }

    public Car addCar(CarDTO dto) {
        Car car = new Car(null, dto.getBrand(), dto.getModel());
        return carRepository.save(car);
    }

    public Car updateCar(Long id, CarDTO dto) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Car with ID " + id + " not found"));

        existingCar.setBrand(dto.getBrand());
        existingCar.setModel(dto.getModel());

        return carRepository.save(existingCar); // save works for both insert & update
    }

    public void deleteCar(Long id) {
        if (!carRepository.existsById(id)) {
            throw new VehicleNotFoundException("Cannot delete. Car not found with ID: " + id);
        }
        carRepository.deleteById(id);
    }
}
