package com.rohlik.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductDTO {
    private String name;
    @Positive
    private BigDecimal price;
    @Min(0)
    private Integer stockQuantity;
}