package com.ecommerce.client.service;

import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ecommerce.client.dto.OrderDto;

@Service
public class OrderServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceClient.class);
    private static final String API_GATEWAY_URL = "http://localhost:8080";
    
    @Autowired
    private RestTemplate restTemplate;
    
    public List<OrderDto> getAllOrders() {
        try {
            logger.info("Fetching all orders via API Gateway");
            OrderDto[] orders = restTemplate.getForObject(
                API_GATEWAY_URL + "/api/orders", 
                OrderDto[].class
            );
            return orders != null ? Arrays.asList(orders) : List.of();
        } catch (Exception e) {
            logger.error("Error fetching orders: {}", e.getMessage());
            return List.of();
        }
    }
    
    public OrderDto getOrderById(Long id) {
        try {
            logger.info("Fetching order with ID: {}", id);
            return restTemplate.getForObject(
                API_GATEWAY_URL + "/api/orders/" + id, 
                OrderDto.class
            );
        } catch (Exception e) {
            logger.error("Error fetching order {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    public OrderDto createOrder(OrderDto order) {
        try {
            logger.info("Creating new order for customer: {}", order.getCustomerEmail());
            return restTemplate.postForObject(
                API_GATEWAY_URL + "/api/orders", 
                order, 
                OrderDto.class
            );
        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage());
            throw new RuntimeException("Failed to create order: " + e.getMessage());
        }
    }
    
    public List<OrderDto> getOrdersByCustomer(String email) {
        try {
            logger.info("Fetching orders for customer: {}", email);
            OrderDto[] orders = restTemplate.getForObject(
                API_GATEWAY_URL + "/api/orders/customer/" + email, 
                OrderDto[].class
            );
            return orders != null ? Arrays.asList(orders) : List.of();
        } catch (Exception e) {
            logger.error("Error fetching customer orders: {}", e.getMessage());
            return List.of();
        }
    }
}