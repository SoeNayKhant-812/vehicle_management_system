package com.example.demo.mapper;

import com.example.demo.dto.MotorcycleDTO;
import com.example.demo.model.Motorcycle;

public class MotorcycleMapper {

	public static MotorcycleDTO toDTO(Motorcycle motorcycle) {
		if (motorcycle == null) {
			return null;
		}
		return new MotorcycleDTO(motorcycle.getId(), motorcycle.getBrand(), motorcycle.getModel());
	}

	public static Motorcycle toEntity(MotorcycleDTO dto) {
		if (dto == null) {
			return null;
		}
		Motorcycle motorcycle = new Motorcycle();
		motorcycle.setId(dto.getId());
		motorcycle.setBrand(dto.getBrand());
		motorcycle.setModel(dto.getModel());
		return motorcycle;
	}
}
