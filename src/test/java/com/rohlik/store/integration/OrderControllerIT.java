package com.rohlik.store.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rohlik.store.dto.CreateOrderDTO;
import com.rohlik.store.dto.ProductIdQuantityDTO;
import com.rohlik.store.model.Order;
import com.rohlik.store.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * Test that checks if the endpoint /api/v1/orders returns
     * a list of orders with the correct structure and values.
     */
    @Test
    void getAllOrders_ShouldReturnOrders() throws Exception {
        System.out.println("Text executing: getAllOrders_ShouldReturnOrders");
        mockMvc.perform(get("/api/v1/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()", is(2))) // There are 2 default orders in the database
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].paid", is(true)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].paid", is(false)));
    }

    /**
     * Test that checks if the endpoint /api/v1/orders/{id} returns
     * the correct order by ID with the correct structure and values.
     */
    @Test
    void getOrderById_ShouldReturnOrder_WhenOrderExists() throws Exception {
        System.out.println("Text executing: getOrderById_ShouldReturnOrder_WhenOrderExists");
        mockMvc.perform(get("/api/v1/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(1)))
                .andExpect(jsonPath("$.paid", is(true)))
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    /**
     * Test that checks if the endpoint /api/v1/orders/{id} returns
     * an error when the order does not exist.
     */
    @Test
    void createOrder_ShouldSucceed_WhenStockIsAvailable() throws Exception {
        System.out.println("Text executing: createOrder_ShouldSucceed_WhenStockIsAvailable");
        ProductIdQuantityDTO item = new ProductIdQuantityDTO();
        item.setProductId(3L); // Radegast Ryze hořká 12
        item.setQuantity(5); // 5 pieces
        CreateOrderDTO newOrder = new CreateOrderDTO();
        newOrder.setItems(new ArrayList<>(List.of(item)));

        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newOrder)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.items", hasSize(1)));
    }

    /**
     * Test that checks the case of updating the order status to paid.
     */
    @Test
    void payOrder_ShouldMarkOrderAsPaid() throws Exception {
        System.out.println("Text executing: payOrder_ShouldMarkOrderAsPaid");
        mockMvc.perform(post("/api/v1/orders/2/pay"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paid", is(true)));

        Order updatedOrder = orderRepository.findById(2L).orElseThrow();
        assert(updatedOrder.isPaid());
    }
}
