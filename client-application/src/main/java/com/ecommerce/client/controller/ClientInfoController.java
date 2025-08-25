package com.ecommerce.client.controller;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientInfoController {
    
    @GetMapping("/info")
    public Map<String, Object> getClientInfo() {
        Map<String, Object> info = new HashMap<>();
        info.put("name", "E-commerce Client Application");
        info.put("version", "1.0.0");
        info.put("description", "REST Client for E-commerce Microservices");
        info.put("port", "8083");
        info.put("timestamp", LocalDateTime.now());
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("products", "/client/products");
        endpoints.put("orders", "/client/orders");
        endpoints.put("health", "/client/health");
        info.put("endpoints", endpoints);
        
        return info;
    }
    
    @GetMapping("/health")
    public Map<String, String> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "client-application");
        health.put("timestamp", LocalDateTime.now().toString());
        return health;
    }
}