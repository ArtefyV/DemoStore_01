package com.rohlik.store.dto;

import lombok.Data;

@Data
public class ProductDTO {
    private Long id;
    private String name;
    private String price;
    private int stockQuantity;
}
