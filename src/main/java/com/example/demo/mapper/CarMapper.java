package com.example.demo.mapper;

import com.example.demo.dto.CarDTO;
import com.example.demo.model.Car;

public class CarMapper {

	public static CarDTO toDTO(Car car) {
		if (car == null) {
			return null;
		}
		return new CarDTO(car.getId(), car.getBrand(), car.getModel());
	}

	public static Car toEntity(CarDTO dto) {
		if (dto == null) {
			return null;
		}
		Car car = new Car();
		car.setId(dto.getId());
		car.setBrand(dto.getBrand());
		car.setModel(dto.getModel());
		return car;
	}
}
