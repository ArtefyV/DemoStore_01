package com.rohlik.store.mapper;

import com.rohlik.store.dto.CreateProductDTO;
import com.rohlik.store.dto.ProductDTO;
import com.rohlik.store.dto.UpdateProductDTO;
import com.rohlik.store.model.Product;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class ProductMapper {

    /**
     * Maps Product entity to ProductDTO
     * @param product Product entity
     * @return ProductDTO
     */
    public ProductDTO toProductDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setPrice(String.valueOf(product.getPrice()));
        dto.setStockQuantity(product.getStockQuantity());
        return dto;
    }

    /**
     * Maps CreateProductDTO to Product entity
     * @param dto CreateProductDTO
     * @return Product entity
     */
    public Product toProduct(CreateProductDTO dto) {
        return mapToProduct(dto.getName(), dto.getPrice(), dto.getStockQuantity());
    }

    /**
     * Maps UpdateProductDTO to Product entity
     * @param dto UpdateProductDTO
     * @return Product entity
     */
    public Product toProduct(UpdateProductDTO dto) {
        return mapToProduct(dto.getName(), dto.getPrice(), dto.getStockQuantity());
    }

    /**
     * Private method to map common fields to Product entity
     * @param name Product name
     * @param price Product price
     * @param stockQuantity Product stock quantity
     * @return Product entity
     */
    private Product mapToProduct(String name, BigDecimal price, Integer stockQuantity) {
        Product product = new Product();
        product.setName(name);
        product.setPrice(price);
        product.setStockQuantity(stockQuantity);
        return product;
    }
}