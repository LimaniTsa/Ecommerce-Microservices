package com.ecommerce.client.controller;

import com.ecommerce.client.dto.ProductDto;
import com.ecommerce.client.service.ProductServiceClient;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/client/products")
public class ClientProductController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientProductController.class);
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    @GetMapping
    public ResponseEntity<?> getAllProducts() {
        try {
            logger.info("Client: Fetching all products");
            List<ProductDto> products = productServiceClient.getAllProducts();
            
            Map<String, Object> response = new HashMap<>();
            response.put("products", products);
            response.put("count", products.size());
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Client: Error fetching products", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch products: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getProductById(@PathVariable Long id) {
        try {
            logger.info("Client: Fetching product with ID: {}", id);
            ProductDto product = productServiceClient.getProductById(id);
            
            if (product == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Product not found with ID: " + id);
                error.put("timestamp", LocalDateTime.now());
                error.put("status", "not_found");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("product", product);
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Client: Error fetching product", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch product: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createProduct(@Valid @RequestBody ProductDto product) {
        try {
            logger.info("Client: Creating new product: {}", product.getName());
            ProductDto createdProduct = productServiceClient.createProduct(product);
            
            Map<String, Object> response = new HashMap<>();
            response.put("product", createdProduct);
            response.put("message", "Product created successfully");
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Client: Error creating product", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create product: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
}