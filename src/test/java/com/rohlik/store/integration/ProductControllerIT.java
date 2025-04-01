package com.rohlik.store.integration;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohlik.store.dto.CreateProductDTO;
import com.rohlik.store.dto.UpdateProductDTO;
import com.rohlik.store.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class ProductControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ProductRepository productRepository;

    @Test
    void getAllProducts_ShouldReturnProducts() throws Exception {
        mockMvc.perform(get("/api/v1/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", greaterThan(0)));
    }

    @Test
    void getProductById_ShouldReturnProduct_WhenProductExists() throws Exception {
        mockMvc.perform(get("/api/v1/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)));
    }

    @Test
    void getProductById_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(get("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createProduct_ShouldSucceed() throws Exception {
        CreateProductDTO newProduct = new CreateProductDTO();
        newProduct.setName("New Product");
        newProduct.setPrice(BigDecimal.valueOf(100.0));
        newProduct.setStockQuantity(25);

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void createProduct_ShouldFail_WhenDataIsInvalid() throws Exception {
        CreateProductDTO newProduct = new CreateProductDTO();
        newProduct.setName("");
        newProduct.setPrice(BigDecimal.valueOf(-100.0));

        mockMvc.perform(post("/api/v1/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newProduct)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateProduct_ShouldSucceed_WhenProductExists() throws Exception {
        UpdateProductDTO updateProduct = new UpdateProductDTO();
        updateProduct.setName("Updated Product");
        updateProduct.setPrice(BigDecimal.valueOf(150.0));

        mockMvc.perform(put("/api/v1/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is("Updated Product")));
    }

    @Test
    void updateProduct_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        UpdateProductDTO updateProduct = new UpdateProductDTO();
        updateProduct.setName("Nonexistent Product");
        updateProduct.setPrice(BigDecimal.valueOf(150.0));

        mockMvc.perform(put("/api/v1/products/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateProduct)))
                .andExpect(status().isNotFound());
    }


    @Test
    void deleteProduct_ShouldSucceed_WhenProductExists() throws Exception {
        mockMvc.perform(delete("/api/v1/products/5"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteProduct_ShouldFail_WhenProductIsInOrder() throws Exception {
        mockMvc.perform(delete("/api/v1/products/1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("Cannot delete product that is associated with existing orders")));
    }

    @Test
    void deleteProduct_ShouldReturnNotFound_WhenProductDoesNotExist() throws Exception {
        mockMvc.perform(delete("/api/v1/products/999"))
                .andExpect(status().isNotFound());
    }
}