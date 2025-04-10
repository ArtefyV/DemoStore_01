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
import com.demo.store.util.RetryUtils;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderExtraRepository orderExtraRepository;
    private final ProductRepository productRepository;
    private final OrderProductRepository orderProductRepository;

    @Value("${order.expiration-time-minutes}")
    private int orderExpirationTime;


    /**
     * Get a list of all orders
     * @return List of all orders
     */
    public List<OrderExtra> getAllOrders() {
        log.info("Getting a list of all orders");
        return orderExtraRepository.findAll();
    }

    /**
     * Get an order by its ID
     * @param id Order ID
     * @return Order object
     */
    public OrderExtra getOrderById(Long id) {
        log.info("Getting order by ID {}", id);
        return orderExtraRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Order with ID {} not found", id);
                    return new NotFoundException("Order not found");
                });
    }

    /**
     * Create a new order
     * @param order Order object for creation
     *              Order ID will be ignored since it is generated by the database
     *              CreatedAt will be set to the current time
     *              Paid status will be set to false
     *
     * @param items List of OrderProduct objects to be added to the order
     *              OrderProduct ID will be ignored since it is generated by the database
     *              Order ID will be set to the ID of the created order
     *              Product ID will be set to the ID of the product
     * @return OrderExtra object to be returned to the client
     */
    @Transactional
    public OrderExtra createOrder(Order order, List<OrderProduct> items) {
        return RetryUtils.retry(
                3,
                200,
                ObjectOptimisticLockingFailureException.class,
                () -> createOrderInternal(order, items),
                NotFoundException.class, InsufficientStockException.class
        );
    }

    private OrderExtra createOrderInternal(Order order, List<OrderProduct> items) {
        log.info("Creating a new order...");
        order.prePersist();
        Order savedOrder = orderRepository.save(order);

        for (OrderProduct item : items) {
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

            item.setOrder(savedOrder);
            item.setProduct(product);
            orderProductRepository.save(item);
        }

        log.info("An order was created with ID {}", savedOrder.getId());
        return new OrderExtra(savedOrder.getId(), savedOrder.getCreatedAt(), savedOrder.isPaid(), items);
    }

    /**
     * Cancel an order by its ID
     * @param id Order ID
     */
    @Transactional
    public void cancelOrder(Long id) {
        log.info("Cancel order with ID {}", id);
        OrderExtra order = orderExtraRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempting to cancel a non-existent order with ID {}", id);
                    return new NotFoundException("Order not found");
                });

        for (OrderProduct item : order.getItems()) {
            Product product = item.getProduct();
            log.info("Return goods {} in quantity {}", product.getName(), item.getQuantity());
            product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
            productRepository.save(product);
        }

        orderExtraRepository.deleteById(id);
        log.info("The order with ID {} has been canceled", id);
    }


    /**
     * Pay for an order
     * @param id Order ID
     * @return Order object with updated paid status
     */
    @Transactional
    public OrderExtra payOrder(Long id) {
        log.info("Paying for order with ID {}", id);
        OrderExtra order = orderExtraRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Attempting to pay for a non-existent order with ID {}", id);
                    return new NotFoundException("Order with ID " + id + " not found");
                });

        if (order.isPaid()) {
            throw new IllegalStateException("Order with ID " + id + " already paid");
        }

        order.setPaid(true);
        Order updatingOrder = new Order(order.getId(), order.getCreatedAt(), order.isPaid());
        orderRepository.save(updatingOrder);
        log.info("The order with ID {} has been successfully paid", id);
        return order;
    }

    /**
     * Get list of orders by paid status
     * @param paid Paid status
     *             true - paid
     *             false - not paid
     * @return List of orders selected by paid status
     */
    public List<OrderExtra> getOrdersByPaidStatus(boolean paid) {
        log.info("Getting a list of orders by paid status: {}", paid);
        return orderExtraRepository.findByPaid(paid);
    }

    /**
     * Cancel expired orders by a scheduled task
     */
    @Scheduled(fixedRateString = "${order.check-interval-ms}")
    @Transactional
    public void cancelExpiredOrders() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(orderExpirationTime);
        List<OrderExtra> expiredOrders = orderExtraRepository.findByPaidFalseAndCreatedAtBefore(threshold);

        for (OrderExtra order : expiredOrders) {
            log.info("Cancel expired order ID {}", order.getId());

            for (OrderProduct item : order.getItems()) {
                Product product = item.getProduct();
                log.info("Return goods {} in quantity {} during cancelling of expired order ID {}", product.getName(), item.getQuantity(), order.getId());
                product.setStockQuantity(product.getStockQuantity() + item.getQuantity());
                productRepository.save(product);
            }

            orderExtraRepository.delete(order);
        }
    }
}
