package com.example.demo.dto;

public class TruckDTO {
    private Long Id;
    private String brand;
    private String model;

    public TruckDTO() {
    }

    public TruckDTO(Long Id, String brand, String model) {
        this.Id = Id;
        this.brand = brand;
        this.model = model;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
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
