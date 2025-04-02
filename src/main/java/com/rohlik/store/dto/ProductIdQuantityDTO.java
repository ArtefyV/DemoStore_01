package com.rohlik.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductIdQuantityDTO {
    @NotNull
    @Schema(description = "ID of the product", example = "1")
    private Long productId;

    @Schema(description = "Quantity of products in the order", example = "100")
    private Integer quantity;
}