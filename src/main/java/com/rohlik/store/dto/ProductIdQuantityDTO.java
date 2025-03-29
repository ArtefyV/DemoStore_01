package com.rohlik.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductIdQuantityDTO {
    @NotNull
    private Long productId;
    private Integer quantity;
}