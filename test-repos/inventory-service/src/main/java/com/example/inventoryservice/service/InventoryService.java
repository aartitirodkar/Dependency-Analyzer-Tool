package com.example.inventoryservice.service;

import com.example.inventoryservice.dto.ProductDTO;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class InventoryService {
    
    public ProductDTO getProductStock(Long productId) {
        return new ProductDTO(productId, "Product " + productId, 100, 50);
    }
    
    public ProductDTO reserveProduct(Long productId, Integer quantity) {
        ProductDTO product = getProductStock(productId);
        product.setReservedQuantity(product.getReservedQuantity() + quantity);
        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
        return product;
    }
    
    public List<ProductDTO> getAllProducts() {
        List<ProductDTO> products = new ArrayList<>();
        products.add(new ProductDTO(1L, "Laptop", 100, 20));
        products.add(new ProductDTO(2L, "Mouse", 200, 50));
        return products;
    }
    
    public boolean validateProduct(String productName) {
        return StringUtils.isNotBlank(productName) && productName.length() > 2;
    }
}

