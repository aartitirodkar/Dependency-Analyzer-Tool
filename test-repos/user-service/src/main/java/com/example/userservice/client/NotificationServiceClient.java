package com.example.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient(name = "notification-service", url = "http://localhost:8083")
public interface NotificationServiceClient {
    
    @PostMapping("/api/notifications/send")
    Map<String, Object> sendNotification(@RequestBody Map<String, Object> notification);
}

