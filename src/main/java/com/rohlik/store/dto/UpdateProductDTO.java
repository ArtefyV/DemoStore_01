package com.rohlik.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductDTO {
    @Schema(description = "Product name", example = "Big Thing")
    private String name;

    @Positive
    @Schema(description = "Product price", example = "19.99")
    private BigDecimal price;

    @Min(0)
    @Schema(description = "Stock quantity", example = "100")
    private Integer stockQuantity;
}