package com.ecommerce.order.client;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import com.ecommerce.order.dto.ProductDto;

@Component
public class ProductServiceFallback implements ProductServiceClient {
    
    @Override
    public ProductDto getProductById(Long id) {
        //return a default product or null when the product service is down
        ProductDto fallbackProduct = new ProductDto();
        fallbackProduct.setId(id);
        fallbackProduct.setName("Product Unavailable");
        fallbackProduct.setPrice(BigDecimal.ZERO);
        fallbackProduct.setStockQuantity(0);
        return fallbackProduct;
    }
}