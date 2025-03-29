package com.rohlik.store.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private LocalDateTime createdAt;
    private boolean paid;
    private List<ProductQuantityDTO> items;
}