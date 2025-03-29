package com.rohlik.store.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderDTO {
    private Boolean paid;

    @NotEmpty
    private List<ProductIdQuantityDTO> items;
}