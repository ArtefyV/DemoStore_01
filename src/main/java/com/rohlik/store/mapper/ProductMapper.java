package com.rohlik.store.mapper;

import com.rohlik.store.dto.ProductDTO;
import com.rohlik.store.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapper {
    public ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(String.valueOf(product.getPrice()));
        dto.setStockQuantity(product.getStockQuantity());
        return dto;
    }

    public Product toProduct(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(BigDecimal.valueOf(Double.parseDouble(dto.getPrice())));
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }
}