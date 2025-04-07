package com.demo.store.service;

import com.demo.store.exception.InsufficientStockException;
import com.demo.store.exception.NotFoundException;
import com.demo.store.model.Order;
import com.demo.store.model.OrderExtra;
import com.demo.store.model.OrderProduct;
import com.demo.store.model.Product;
import com.demo.store.repository.OrderExtraRepository;
import com.demo.store.repository.OrderProductRepository;
import com.demo.store.repository.OrderRepository;
import com.demo.store.repository.ProductRepository;
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
    private OrderExtraRepository orderExtraRepository;

    @Mock
    private OrderProductRepository orderProductRepository;

    @InjectMocks
    private OrderService orderService;

    private Order order;
    private OrderExtra orderExtra;
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
        orderExtra = new OrderExtra();
        orderExtra.setItems(items);
    }

    /**
     * Order creation test
     */
    @Test
    void createOrder_ShouldSucceed_WhenStockIsAvailable() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(orderProductRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        OrderExtra createdOrder = orderService.createOrder(order, items);

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
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.createOrder(order, items));

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

        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> orderService.createOrder(order, items));
        assertTrue(exception.getMessage().contains("Insufficient stock"));

        verify(orderProductRepository, never()).save(any(OrderProduct.class));
    }

    /**
     * Test that checks the case of updating the order status to paid.
     */
    @Test
    void payOrder_ShouldMarkOrderAsPaid() {
        when(orderExtraRepository.findById(1L)).thenReturn(Optional.of(orderExtra));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OrderExtra paidOrder = orderService.payOrder(1L);

        assertNotNull(paidOrder);
        assertTrue(paidOrder.isPaid());
        verify(orderRepository, times(1)).save(any(Order.class));
    }

    /**
     * Test that checks the case of updating the order status, when the order does not exist.
     */
    @Test
    void payOrder_ShouldThrowException_WhenOrderDoesNotExist() {
        when(orderExtraRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.payOrder(1L));

        verify(orderRepository, never()).save(any(Order.class));
    }

    /**
     * Test of getting a correct order by ID.
     */
    @Test
    void getOrderById_ShouldReturnOrder_WhenOrderExists() {
        when(orderExtraRepository.findById(1L)).thenReturn(Optional.of(orderExtra));

        OrderExtra foundOrder = orderService.getOrderById(1L);

        assertNotNull(foundOrder);
        assertEquals(orderExtra, foundOrder);
        verify(orderExtraRepository, times(1)).findById(1L);
    }

    /**
     * Test of getting an order by ID, when the order does not exist.
     */
    @Test
    void getOrderById_ShouldThrowException_WhenOrderDoesNotExist() {
        when(orderExtraRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.getOrderById(1L));

        verify(orderExtraRepository, times(1)).findById(1L);
    }

    /**
     * Test of getting all orders.
     */
    @Test
    void getAllOrders_ShouldReturnOrders() {
        when(orderExtraRepository.findAll()).thenReturn(List.of(orderExtra, orderExtra));

        List<OrderExtra> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertEquals(2, orders.size());
        verify(orderExtraRepository, times(1)).findAll();
    }

    /**
     * Test of getting all orders, when there are no orders.
     */
    @Test
    void getAllOrders_ShouldReturnEmptyList_WhenNoOrders() {
        when(orderExtraRepository.findAll()).thenReturn(Collections.emptyList());

        List<OrderExtra> orders = orderService.getAllOrders();

        assertNotNull(orders);
        assertTrue(orders.isEmpty());
        verify(orderExtraRepository, times(1)).findAll();
    }

    /**
     * Test of cancelling an order by ID.
     */
    @Test
    void cancelOrder_ShouldSucceed_WhenOrderExists() {
        when(orderExtraRepository.findById(1L)).thenReturn(Optional.of(orderExtra));

        orderService.cancelOrder(1L);

        verify(orderExtraRepository, times(1)).deleteById(1L);
    }

    /**
     * Test of cancelling an order by ID, when the order does not exist.
     */
    @Test
    void cancelOrder_ShouldThrowException_WhenOrderDoesNotExist() {
        when(orderExtraRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> orderService.cancelOrder(1L));

        verify(orderExtraRepository, never()).deleteById(1L);
    }


}
