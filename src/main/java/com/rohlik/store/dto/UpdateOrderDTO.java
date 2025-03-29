package com.rohlik.store.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdateOrderDTO {
    @NotNull
    private Long id;
    private Boolean paid;
    private List<ProductIdQuantityDTO> items;
}