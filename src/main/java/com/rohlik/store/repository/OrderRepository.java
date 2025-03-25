package com.rohlik.store.repository;

import com.rohlik.store.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByPaid(boolean paid); // To get all orders by paid status
}