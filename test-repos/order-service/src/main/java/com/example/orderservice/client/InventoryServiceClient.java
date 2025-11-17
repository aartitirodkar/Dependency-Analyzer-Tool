package com.example.orderservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "inventory-service", url = "http://localhost:8085")
public interface InventoryServiceClient {
    
    @GetMapping("/api/inventory/{productId}")
    Map<String, Object> getProductStock(@PathVariable Long productId);
    
    @PutMapping("/api/inventory/{productId}/reserve")
    Map<String, Object> reserveProduct(@PathVariable Long productId, @RequestBody Map<String, Object> request);
}

