package com.example.notificationservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class NotificationDTO {
    private Long id;
    private String type;
    private String message;
    private String status;
    
    public NotificationDTO() {}
    
    public NotificationDTO(Long id, String type, String message, String status) {
        this.id = id;
        this.type = type;
        this.message = message;
        this.status = status;
    }
    
    @JsonProperty("id")
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    @JsonProperty("type")
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    @JsonProperty("message")
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    @JsonProperty("status")
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}

