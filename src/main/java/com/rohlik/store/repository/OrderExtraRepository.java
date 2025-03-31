package com.rohlik.store.repository;

import com.rohlik.store.model.OrderExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface OrderExtraRepository extends JpaRepository<OrderExtra, Long> {
    List<OrderExtra> findByPaid(boolean paid);
    List<OrderExtra> findByPaidFalseAndCreatedAtBefore(LocalDateTime threshold);
}