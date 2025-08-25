package com.ecommerce.product.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.repository.ProductRepository;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;
    
    // create or update product
    public Product saveProduct(Product product) {
        return productRepository.save(product);
    }
    
    // get all products
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }
    
    // get product by ID
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    
    // delete product
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    // get products by category
    public List<Product> getProductsByCategory(String category) {
        return productRepository.findByCategory(category);
    }
    
    // search products by name
    public List<Product> searchProductsByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name);
    }
    
    // get available products
    public List<Product> getAvailableProducts() {
        return productRepository.findProductsWithStock(0);
    }
    
    // update stock quantity
    public Product updateStock(Long productId, Integer newStock) {
        Optional<Product> productOpt = productRepository.findById(productId);
        if (productOpt.isPresent()) {
            Product product = productOpt.get();
            product.setStockQuantity(newStock);
            return productRepository.save(product);
        }
        throw new RuntimeException("Product not found with id: " + productId);
    }
}