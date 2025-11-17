package com.example.inventoryservice.controller;

import com.example.inventoryservice.dto.ProductDTO;
import com.example.inventoryservice.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {
    
    @Autowired
    private InventoryService inventoryService;
    
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductStock(@PathVariable Long productId) {
        return ResponseEntity.ok(inventoryService.getProductStock(productId));
    }
    
    @PutMapping("/{productId}/reserve")
    public ResponseEntity<ProductDTO> reserveProduct(@PathVariable Long productId, @RequestBody java.util.Map<String, Object> request) {
        return ResponseEntity.ok(inventoryService.reserveProduct(productId, (Integer) request.get("quantity")));
    }
    
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        return ResponseEntity.ok(inventoryService.getAllProducts());
    }
}

