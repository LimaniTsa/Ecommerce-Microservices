package com.ecommerce.order.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.order.entity.Order;
import com.ecommerce.order.entity.OrderStatus;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    //find orders by customer email
    List<Order> findByCustomerEmail(String customerEmail);
    
    // find orders by status
    List<Order> findByStatus(OrderStatus status);
    
    //find orders by customer name containing
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);
    
    //find orders created between dates
    @Query("SELECT o FROM Order o WHERE o.createdAt BETWEEN :startDate AND :endDate")
    List<Order> findOrdersBetweenDates(@Param("startDate") LocalDateTime startDate, 
                                      @Param("endDate") LocalDateTime endDate);
}