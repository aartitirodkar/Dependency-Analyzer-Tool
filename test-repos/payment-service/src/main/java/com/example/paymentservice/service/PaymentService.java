package com.example.paymentservice.service;

import com.example.paymentservice.client.NotificationServiceClient;
import com.example.paymentservice.dto.PaymentDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class PaymentService {
    
    @Autowired
    private NotificationServiceClient notificationServiceClient;
    
    public PaymentDTO processPayment(PaymentDTO payment) {
        // Mock payment processing
        payment.setStatus("PROCESSED");
        payment.setTransactionId("TXN-" + System.currentTimeMillis());
        
        // Send notification
        Map<String, Object> notification = new HashMap<>();
        notification.put("type", "PAYMENT_SUCCESS");
        notification.put("message", "Payment processed successfully");
        notificationServiceClient.sendNotification(notification);
        
        return payment;
    }
    
    public PaymentDTO getPaymentById(Long id) {
        return new PaymentDTO(id, 100.0, "PROCESSED", "TXN-" + id);
    }
    
    public boolean validatePayment(String cardNumber) {
        return StringUtils.isNotBlank(cardNumber) && cardNumber.length() >= 16;
    }
}

