package com.example.orderservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class OrderDTO {
    private Long id;
    private String orderNumber;
    private Double amount;
    
    public OrderDTO() {}
    
    public OrderDTO(Long id, String orderNumber, Double amount) {
        this.id = id;
        this.orderNumber = orderNumber;
        this.amount = amount;
    }
    
    @JsonProperty("id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @JsonProperty("orderNumber")
    public String getOrderNumber() {
        return orderNumber;
    }
    
    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }
    
    @JsonProperty("amount")
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
}

