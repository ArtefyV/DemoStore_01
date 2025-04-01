package com.rohlik.store.service;

import com.rohlik.store.exception.InsufficientStockException;
import com.rohlik.store.exception.NotFoundException;
import com.rohlik.store.model.Order;
import com.rohlik.store.model.OrderExtra;
import com.rohlik.store.model.OrderProduct;
import com.rohlik.store.model.Product;
import com.rohlik.store.repository.OrderProductRepository;
import com.rohlik.store.repository.OrderRepository;
import com.rohlik.store.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private Product product;
    private OrderProduct orderProduct;
    private List<OrderProduct> items;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPrice(BigDecimal.valueOf(19.99));
        product.setStockQuantity(10);

        orderProduct = new OrderProduct();
        orderProduct.setProduct(product);
        orderProduct.setQuantity(5);

        items = Collections.singletonList(orderProduct);

        order = new Order();
    }

    /**
     * Order creation test
     */
    @Test
    void createOrder_ShouldSucceed_WhenStockIsAvailable() {
        // Preparation of moks
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderProductRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Attempting to create an order
        OrderExtra createdOrder = orderService.createOrder(order, items);

        // Checks
        assertNotNull(createdOrder);
        assertEquals(5, product.getStockQuantity()); // Было 10, заказали 5 → осталось 5
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(productRepository, times(1)).save(any(Product.class));
    }

    /**
     * Try to create order, when product is not found
     */
    @Test
    void createOrder_ShouldThrowException_WhenProductNotFound() {
        // Simulate absence of the product in the database
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotFoundException.class, () -> orderService.createOrder(order, items));

        // Checking method calls
        verify(orderRepository, times(1)).save(any(Order.class));
        verify(orderProductRepository, never()).save(any(OrderProduct.class));
    }

    /**
     * Try to create order, when stock is insufficient
     */
    @Test
    void createOrder_ShouldThrowException_WhenStockIsInsufficient() {
        product.setStockQuantity(2); // Reduce the stock balance from 10 to 2

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // Check if the expected exception is thrown
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> orderService.createOrder(order, items));
        assertTrue(exception.getMessage().contains("Insufficient stock"));

        // Checking method call
        verify(orderProductRepository, never()).save(any(OrderProduct.class));
    }
}
