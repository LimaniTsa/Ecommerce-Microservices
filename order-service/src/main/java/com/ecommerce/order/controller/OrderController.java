package com.ecommerce.order.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ecommerce.order.dto.CreateOrderRequest;
import com.ecommerce.order.dto.OrderItemRequest;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.service.OrderService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/orders")
public class OrderController {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderController.class);
    
    @Autowired
    private OrderService orderService;
    
    // get all orders
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        logger.info("Fetching all orders");
        List<Order> orders = orderService.getAllOrders();
        return ResponseEntity.ok(orders);
    }
    
    // get order by id
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
        logger.info("Fetching order with ID: {}", id);
        Optional<Order> order = orderService.getOrderById(id);
        return order.map(ResponseEntity::ok)
                   .orElse(ResponseEntity.notFound().build());
    }
    
    // create new order
    @PostMapping
    public ResponseEntity<?> createOrder(@Valid @RequestBody CreateOrderRequest orderRequest) {
        try {
            logger.info("Creating new order for customer: {}", orderRequest.getCustomerEmail());
            
            // Convert DTO to entities
            Order order = new Order();
            order.setCustomerEmail(orderRequest.getCustomerEmail());
            order.setCustomerName(orderRequest.getCustomerName());
            
            List<OrderItem> orderItems = new ArrayList<>();
            for (OrderItemRequest itemRequest : orderRequest.getOrderItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(itemRequest.getProductId());
                orderItem.setQuantity(itemRequest.getQuantity());
                orderItems.add(orderItem);
            }
            order.setOrderItems(orderItems);
            
            Order savedOrder = orderService.createOrder(order);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedOrder);
            
        } catch (Exception e) {
            logger.error("Error creating order", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }
    
    // get orders by customer email
    @GetMapping("/customer/{email}")
    public ResponseEntity<List<Order>> getOrdersByCustomerEmail(@PathVariable("email") String email) {
        logger.info("Fetching orders for customer: {}", email);
        List<Order> orders = orderService.getOrdersByCustomerEmail(email);
        return ResponseEntity.ok(orders);
    }
    
    // get orders by status
    @GetMapping("/status/{status}")
    public ResponseEntity<List<Order>> getOrdersByStatus(@PathVariable("status") OrderStatus status) {
        logger.info("Fetching orders with status: {}", status);
        List<Order> orders = orderService.getOrdersByStatus(status);
        return ResponseEntity.ok(orders);
    }
    
    // update order status
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateOrderStatus(@PathVariable("id") Long id, @RequestParam OrderStatus status) {
        try {
            logger.info("Updating order {} status to {}", id, status);
            Order updatedOrder = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(updatedOrder);
        } catch (Exception e) {
            logger.error("Error updating order status", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    // delete order
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteOrder(@PathVariable("id") Long id) {
        try {
            logger.info("Cancelling order: {}", id);
            orderService.deleteOrder(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Order cancelled successfully");
            response.put("orderId", id.toString());
            response.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error cancelling order", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            error.put("timestamp", LocalDateTime.now().toString());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }
    
    // get order items
    @GetMapping("/{id}/items")
    public ResponseEntity<List<OrderItem>> getOrderItems(@PathVariable("id") Long id) {
        logger.info("Fetching items for order: {}", id);
        List<OrderItem> orderItems = orderService.getOrderItems(id);
        return ResponseEntity.ok(orderItems);
    }
    
    // health check endpoint
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> health() {
        Map<String, String> health = new HashMap<>();
        health.put("status", "UP");
        health.put("service", "order-service");
        health.put("timestamp", LocalDateTime.now().toString());
        return ResponseEntity.ok(health);
    }
}