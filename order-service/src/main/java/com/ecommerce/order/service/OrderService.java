package com.ecommerce.order.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.ecommerce.order.client.ProductServiceClient;
import com.ecommerce.order.dto.ProductDto;
import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderItem;
import com.ecommerce.order.entity.OrderStatus;
import com.ecommerce.order.repository.OrderItemRepository;
import com.ecommerce.order.repository.OrderRepository;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Service
@Transactional
public class OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private OrderItemRepository orderItemRepository;
    
    @Autowired
    private ProductServiceClient productServiceClient;
    
    @CircuitBreaker(name = "product-service", fallbackMethod = "createOrderFallback")
    @Retry(name = "product-service")
    public Order createOrder(Order order) {
        logger.info("Creating order for customer: {}", order.getCustomerEmail());
        
        //calculate total amount from order items
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (OrderItem item : order.getOrderItems()) {
            //get product information from Product Service
            ProductDto product = productServiceClient.getProductById(item.getProductId());
            
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new RuntimeException("Insufficient stock for product: " + product.getName());
            }
            
            //set product details in order item
            item.setProductName(product.getName());
            item.setUnitPrice(product.getPrice());
            item.setTotalPrice(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
            item.setOrder(order);
            
            totalAmount = totalAmount.add(item.getTotalPrice());
        }
        
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);
        logger.info("Order created successfully with ID: {}", savedOrder.getId());
        
        return savedOrder;
    }
    
    //fallback method for circuit breaker
    public Order createOrderFallback(Order order, Exception ex) {
        logger.error("Product service unavailable, creating fallback response", ex);
        throw new RuntimeException("Product service is currently unavailable. Please try again later.");
    }
    
    //get all orders
    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    //get order by ID
    @Transactional(readOnly = true)
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    //get orders by customer email
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerEmail(String email) {
        return orderRepository.findByCustomerEmail(email);
    }
    
    // get orders by status
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    // update order status
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            order.setStatus(newStatus);
            Order updatedOrder = orderRepository.save(order);
            logger.info("Order {} status updated to {}", orderId, newStatus);
            return updatedOrder;
        }
        throw new RuntimeException("Order not found with id: " + orderId);
    }
    
    // delete order
    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
    
    // get order items for an order
    @Transactional(readOnly = true)
    public List<OrderItem> getOrderItems(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }
}