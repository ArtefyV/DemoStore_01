package com.rohlik.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDTO {
    @Schema(description = "Payment status of the order", example = "false")
    private Boolean paid;

    @NotEmpty
    @Schema(description = "List of products in the order")
    private List<ProductIdQuantityDTO> items;
}