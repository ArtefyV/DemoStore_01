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
     * Maps ProductDTO to Product entity
     * @param dto ProductDTO
     * @return Product entity
     */
    public Product toProduct(ProductDTO dto) {
        Product product = new Product();
        product.setId(dto.getId());
        product.setName(dto.getName());
        product.setPrice(BigDecimal.valueOf(Double.parseDouble(dto.getPrice())));
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }

    /**
     * Maps CreateProductDTO to Product entity
     * @param dto CreateProductDTO
     * @return Product entity
     */
    public Product toProduct(CreateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }

    /**
     * Maps UpdateProductDTO to Product entity
     * @param dto UpdateProductDTO
     * @return Product entity
     */
    public Product toProduct(UpdateProductDTO dto) {
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStockQuantity(dto.getStockQuantity());
        return product;
    }
}