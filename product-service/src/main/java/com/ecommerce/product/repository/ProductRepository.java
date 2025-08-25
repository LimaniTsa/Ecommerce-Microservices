package com.ecommerce.product.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.ecommerce.product.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    //find products by category
    List<Product> findByCategory(String category);
    
    //find products by name containing (case insensitive)
    List<Product> findByNameContainingIgnoreCase(String name);
    
    // find products with stock quantity greater than specified amount
    @Query("SELECT p FROM Product p WHERE p.stockQuantity > :minStock")
    List<Product> findProductsWithStock(@Param("minStock") Integer minStock);
    
    // find products by category and stock availability
    @Query("SELECT p FROM Product p WHERE p.category = :category AND p.stockQuantity > 0")
    List<Product> findAvailableProductsByCategory(@Param("category") String category);
}