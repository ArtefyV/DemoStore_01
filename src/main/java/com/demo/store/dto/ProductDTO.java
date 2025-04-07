package com.demo.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class ProductDTO {
    @Schema(description = "ID of the product", example = "1")
    private Long id;

    @Schema(description = "Product name", example = "Big Thing")
    private String name;

    @Schema(description = "Product price", example = "19.99")
    private String price;

    @Schema(description = "Stock quantity", example = "100")
    private int stockQuantity;
}
