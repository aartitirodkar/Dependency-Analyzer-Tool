package com.example.userservice.service;

import com.example.userservice.client.OrderServiceClient;
import com.example.userservice.dto.UserDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    public List<UserDTO> getAllUsers() {
        List<UserDTO> users = new ArrayList<>();
        // Mock data
        users.add(new UserDTO(1L, "John Doe", "john@example.com"));
        users.add(new UserDTO(2L, "Jane Smith", "jane@example.com"));
        return users;
    }
    
    public UserDTO getUserById(Long id) {
        return new UserDTO(id, "User " + id, "user" + id + "@example.com");
    }
    
    public boolean validateUser(String email) {
        return StringUtils.isNotBlank(email) && email.contains("@");
    }
}

