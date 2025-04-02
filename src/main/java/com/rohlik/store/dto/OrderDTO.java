package com.rohlik.store.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    @Schema(description = "ID of the order", example = "1")
    private Long id;

    @Schema(description = "Creation date of the order", example = "2021-01-01T12:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Payment status of the order", example = "false")
    private boolean paid;

    @Schema(description = "List of products in the order")
    private List<ProductQuantityDTO> items;
}