package com.example.notificationservice.service;

import com.example.notificationservice.dto.NotificationDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    
    public Map<String, Object> sendNotification(Map<String, Object> notification) {
        // Mock notification sending
        Map<String, Object> response = new HashMap<>();
        response.put("status", "SENT");
        response.put("notificationId", "NOTIF-" + System.currentTimeMillis());
        response.put("message", "Notification sent successfully");
        return response;
    }
    
    public List<NotificationDTO> getAllNotifications() {
        List<NotificationDTO> notifications = new ArrayList<>();
        notifications.add(new NotificationDTO(1L, "EMAIL", "Welcome email", "SENT"));
        notifications.add(new NotificationDTO(2L, "SMS", "Order confirmation", "SENT"));
        return notifications;
    }
    
    public boolean validateNotification(String type) {
        return StringUtils.isNotBlank(type) && (type.equals("EMAIL") || type.equals("SMS"));
    }
}

