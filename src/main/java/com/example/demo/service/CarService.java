package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.exception.VehicleNotFoundException;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import com.example.demo.service.log_service.CarLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CarLogService carLogService;

    @Transactional(readOnly = true)
    public List<Car> getAllCars() {
        return carRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Car not found with ID: " + id));
    }

    @Transactional
    public Car addCar(CarDTO dto) {
        Car car = new Car(null, dto.getBrand(), dto.getModel());
        Car savedCar = carRepository.save(car);

        carLogService.logCarAction(savedCar, "CREATE", "system-user-id", "system");

        return savedCar;
    }

    @Transactional
    public Car updateCar(Long id, CarDTO dto) {
        Car existingCar = carRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Car with ID " + id + " not found"));

        existingCar.setBrand(dto.getBrand());
        existingCar.setModel(dto.getModel());
        Car updatedCar = carRepository.save(existingCar);

        carLogService.logCarAction(updatedCar, "UPDATE", "system-user-id", "system");

        return updatedCar;
    }

    @Transactional
    public void deleteCar(Long id) {
        Car carToDelete = carRepository.findById(id)
                .orElseThrow(() -> new VehicleNotFoundException("Cannot delete. Car not found with ID: " + id));

        carRepository.deleteById(id);

        carLogService.logCarAction(carToDelete, "DELETE", "system-user-id", "system");
    }
}
