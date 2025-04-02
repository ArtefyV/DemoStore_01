package com.rohlik.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductQuantityDTO {
    @Schema(description = "ID of the product", example = "123")
    private int quantity;

    @Schema(description = "Product details")
    private ProductDTO product;
}