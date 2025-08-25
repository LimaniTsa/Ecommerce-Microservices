package com.ecommerce.client.controller;

import com.ecommerce.client.dto.OrderDto;
import com.ecommerce.client.service.OrderServiceClient;
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
@RequestMapping("/client/orders")
public class ClientOrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(ClientOrderController.class);
    
    @Autowired
    private OrderServiceClient orderServiceClient;
    
    @GetMapping
    public ResponseEntity<?> getAllOrders() {
        try {
            logger.info("Client: Fetching all orders");
            List<OrderDto> orders = orderServiceClient.getAllOrders();
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("count", orders.size());
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Client: Error fetching orders", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch orders: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            logger.info("Client: Fetching order with ID: {}", id);
            OrderDto order = orderServiceClient.getOrderById(id);
            
            if (order == null) {
                Map<String, Object> error = new HashMap<>();
                error.put("error", "Order not found with ID: " + id);
                error.put("timestamp", LocalDateTime.now());
                error.put("status", "not_found");
                return ResponseEntity.notFound().build();
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("order", order);
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Client: Error fetching order", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch order: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
    
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody OrderDto order) {
        try {
            logger.info("Client: Creating new order for customer: {}", order.getCustomerEmail());
            OrderDto createdOrder = orderServiceClient.createOrder(order);
            
            Map<String, Object> response = new HashMap<>();
            response.put("order", createdOrder);
            response.put("message", "Order created successfully");
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Client: Error creating order", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to create order: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    @GetMapping("/customer/{email}")
    public ResponseEntity<?> getOrdersByCustomer(@PathVariable String email) {
        try {
            logger.info("Client: Fetching orders for customer: {}", email);
            List<OrderDto> orders = orderServiceClient.getOrdersByCustomer(email);
            
            Map<String, Object> response = new HashMap<>();
            response.put("orders", orders);
            response.put("customerEmail", email);
            response.put("count", orders.size());
            response.put("timestamp", LocalDateTime.now());
            response.put("status", "success");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Client: Error fetching customer orders", e);
            Map<String, Object> error = new HashMap<>();
            error.put("error", "Failed to fetch customer orders: " + e.getMessage());
            error.put("timestamp", LocalDateTime.now());
            error.put("status", "error");
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
}