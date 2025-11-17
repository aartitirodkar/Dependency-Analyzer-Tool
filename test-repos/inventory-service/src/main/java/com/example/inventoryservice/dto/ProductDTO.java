package com.example.inventoryservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ProductDTO {
    private Long id;
    private String name;
    private Integer availableQuantity;
    private Integer reservedQuantity;
    
    public ProductDTO() {}
    
    public ProductDTO(Long id, String name, Integer availableQuantity, Integer reservedQuantity) {
        this.id = id;
        this.name = name;
        this.availableQuantity = availableQuantity;
        this.reservedQuantity = reservedQuantity;
    }
    
    @JsonProperty("id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @JsonProperty("name")
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    @JsonProperty("availableQuantity")
    public Integer getAvailableQuantity() {
        return availableQuantity;
    }
    
    public void setAvailableQuantity(Integer availableQuantity) {
        this.availableQuantity = availableQuantity;
    }
    
    @JsonProperty("reservedQuantity")
    public Integer getReservedQuantity() {
        return reservedQuantity;
    }
    
    public void setReservedQuantity(Integer reservedQuantity) {
        this.reservedQuantity = reservedQuantity;
    }
}

