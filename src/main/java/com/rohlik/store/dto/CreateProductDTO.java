package com.rohlik.store.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class CreateProductDTO {
    @NotBlank(message = "Product name is mandatory")
    private String name;

    @NotNull(message = "Product price is mandatory")
    @Min(value = 0, message = "Product price must be greater than or equal to 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is mandatory")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    private Integer stockQuantity;
}