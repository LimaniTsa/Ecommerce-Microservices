package com.ecommerce.client.service;

import com.ecommerce.client.dto.ProductDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
public class ProductServiceClient {
    
    private static final Logger logger = LoggerFactory.getLogger(ProductServiceClient.class);
    private static final String API_GATEWAY_URL = "http://localhost:8080";
    
    @Autowired
    private RestTemplate restTemplate;
    
    public List<ProductDto> getAllProducts() {
        try {
            logger.info("Fetching all products via API Gateway");
            ProductDto[] products = restTemplate.getForObject(
                API_GATEWAY_URL + "/api/products", 
                ProductDto[].class
            );
            return products != null ? Arrays.asList(products) : List.of();
        } catch (Exception e) {
            logger.error("Error fetching products: {}", e.getMessage());
            return List.of();
        }
    }
    
    public ProductDto getProductById(Long id) {
        try {
            logger.info("Fetching product with ID: {}", id);
            return restTemplate.getForObject(
                API_GATEWAY_URL + "/api/products/" + id, 
                ProductDto.class
            );
        } catch (Exception e) {
            logger.error("Error fetching product {}: {}", id, e.getMessage());
            return null;
        }
    }
    
    public ProductDto createProduct(ProductDto product) {
        try {
            logger.info("Creating new product: {}", product.getName());
            return restTemplate.postForObject(
                API_GATEWAY_URL + "/api/products", 
                product, 
                ProductDto.class
            );
        } catch (Exception e) {
            logger.error("Error creating product: {}", e.getMessage());
            throw new RuntimeException("Failed to create product: " + e.getMessage());
        }
    }
}