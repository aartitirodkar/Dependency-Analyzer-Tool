package com.example.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.Map;

@FeignClient(name = "order-service", url = "http://localhost:8082")
public interface OrderServiceClient {
    
    @GetMapping("/api/orders/user/{userId}")
    List<Map<String, Object>> getUserOrders(@PathVariable Long userId);
}

