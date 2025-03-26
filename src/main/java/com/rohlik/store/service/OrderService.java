package com.rohlik.store.service;

import com.rohlik.store.exception.InsufficientStockException;
import com.rohlik.store.exception.NotFoundException;
import com.rohlik.store.model.Order;
import com.rohlik.store.model.OrderItem;
import com.rohlik.store.model.Product;
import com.rohlik.store.repository.OrderRepository;
import com.rohlik.store.repository.ProductRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    /**
     * Constructor
     * @param orderRepository Order repository
     * @param productRepository Product repository
     */
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    /**
     * Get a list of all orders
     * @return List of all orders
     */
    public List<Order> getAllOrders() {
        log.info("Getting a list of all orders");
        return orderRepository.findAll();
    }

    /**
     * Get an order by its ID
     * @param id Order ID
     * @return Order object
     */
    public Order getOrderById(Long id) {
        log.info("Getting order by ID {}", id);
        return orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order with ID {} not found", id);
                    return new NotFoundException("Order not found");
                });
    }

    /**
     * Create a new order
     * @param order
     * @return
     */
    @Transactional
    public Order createOrder(Order order) {
        log.info("Creating a new order...");
        for (OrderItem item : order.getItems()) {
            Product product = productRepository.findById(item.getProduct().getId())
                    .orElseThrow(() -> {
                        log.warn("Product with ID {} not found, order cannot be created", item.getProduct().getId());
                        return new NotFoundException("Product not found");
                    });

            if (product.getStockQuantity() < item.getQuantity()) {
                log.warn("Out of stock: {} (available {}, requested {})",
                        product.getName(), product.getStockQuantity(), item.getQuantity());
                throw new InsufficientStockException("Insufficient stock: " + product.getName());
            }

            log.info("Reserving item {} in quantity {}", product.getName(), item.getQuantity());
            product.setStockQuantity(product.getStockQuantity() - item.getQuantity());
            productRepository.save(product);
        }

        order.setPaid(false);
        Order savedOrder = orderRepository.save(order);
        log.info("An order was created with ID {}", savedOrder.getId());
        return savedOrder;
    }

    /**
     * Cancel an order
     * @param id Order ID
     */
    @Transactional
    public void cancelOrder(Long id) {
        log.info("Cancel order with ID {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempting to cancel a non-existent order with ID {}", id);
                    return new NotFoundException("Order not found");
                });

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            log.info("Return goods {} in quantity {}", product.getName(), item.getQuantity());
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.deleteById(id);
        log.info("The order with ID {} has been canceled", id);
    }


    /**
     * Pay for an order
     * @param id Order ID
     * @return Order object
     */
    public Order payOrder(Long id) {
        log.info("Paying for order with ID {}", id);
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempting to pay for a non-existent order with ID {}", id);
                    return new NotFoundException("Order with ID " + id + " not found");
                });

        if (order.isPaid()) {
            throw new IllegalStateException("Order with ID " + id + " already paid");
        }

        order.setPaid(true);
        log.info("The order with ID {} has been successfully paid", id);
        return orderRepository.save(order);
    }

    /**
     * Get list of orders by paid status
     * @param paid Paid status
     *             true - paid
     *             false - not paid
     * @return List of orders
     */
    public List<Order> getOrdersByPaidStatus(boolean paid) {
        log.info("Getting a list of orders by paid status: {}", paid);
        return orderRepository.findByPaid(paid);
    }
}
