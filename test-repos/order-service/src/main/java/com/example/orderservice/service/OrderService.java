package com.example.orderservice.service;

import com.example.orderservice.client.UserServiceClient;
import com.example.orderservice.dto.OrderDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {
    
    @Autowired
    private UserServiceClient userServiceClient;
    
    public List<OrderDTO> getAllOrders() {
        List<OrderDTO> orders = new ArrayList<>();
        // Mock data
        orders.add(new OrderDTO(1L, "ORDER-001", 100.50));
        orders.add(new OrderDTO(2L, "ORDER-002", 250.75));
        return orders;
    }
    
    public OrderDTO getOrderById(Long id) {
        return new OrderDTO(id, "ORDER-" + id, 99.99);
    }
    
    public boolean validateOrder(String orderNumber) {
        return StringUtils.isNotBlank(orderNumber) && orderNumber.startsWith("ORDER-");
    }
}

