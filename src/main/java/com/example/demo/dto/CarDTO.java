package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CarDTO {

	private String id;

	@NotBlank(message = "Car brand is required")
	@Size(min = 2, max = 50, message = "Brand must be between 2 and 50 characters")
	private String brand;

	@NotBlank(message = "Car model is required")
	@Size(min = 1, max = 50, message = "Model must be between 1 and 50 characters")
	private String model;

	public CarDTO() {
	}

	public CarDTO(String id, String brand, String model) {
		this.id = id;
		this.brand = brand;
		this.model = model;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getBrand() {
		return brand;
	}

	public void setBrand(String brand) {
		this.brand = brand;
	}

	public String getModel() {
		return model;
	}

	public void setModel(String model) {
		this.model = model;
	}
}
