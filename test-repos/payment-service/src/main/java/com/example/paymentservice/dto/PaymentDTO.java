package com.example.paymentservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PaymentDTO {
    private Long id;
    private Double amount;
    private String status;
    private String transactionId;
    
    public PaymentDTO() {}
    
    public PaymentDTO(Long id, Double amount, String status, String transactionId) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.transactionId = transactionId;
    }
    
    @JsonProperty("id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @JsonProperty("amount")
    public Double getAmount() {
        return amount;
    }
    
    public void setAmount(Double amount) {
        this.amount = amount;
    }
    
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @JsonProperty("transactionId")
    public String getTransactionId() {
        return transactionId;
    }
    
    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }
}

