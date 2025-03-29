package com.rohlik.store.dto;

import lombok.Data;

@Data
public class ProductQuantityDTO {
    private int quantity;
    private ProductDTO product;
}