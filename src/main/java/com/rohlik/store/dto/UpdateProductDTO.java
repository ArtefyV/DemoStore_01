package com.rohlik.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UpdateProductDTO {
    @NotNull
    private Long id;

    private String name;
    private BigDecimal price;
    private Integer stockQuantity;
}