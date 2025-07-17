package com.example.demo.mapper;

import com.example.demo.dto.TruckDTO;
import com.example.demo.model.Truck;

public class TruckMapper {

	public static TruckDTO toDTO(Truck truck) {
		if (truck == null) {
			return null;
		}
		return new TruckDTO(truck.getId(), truck.getBrand(), truck.getModel());
	}

	public static Truck toEntity(TruckDTO dto) {
		if (dto == null) {
			return null;
		}
		Truck truck = new Truck();
		truck.setId(dto.getId());
		truck.setBrand(dto.getBrand());
		truck.setModel(dto.getModel());
		return truck;
	}
}
