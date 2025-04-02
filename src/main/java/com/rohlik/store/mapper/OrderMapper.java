package com.rohlik.store.mapper;

import com.rohlik.store.dto.CreateOrderDTO;
import com.rohlik.store.dto.OrderDTO;
import com.rohlik.store.dto.ProductQuantityDTO;
import com.rohlik.store.model.Order;
import com.rohlik.store.model.OrderExtra;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderMapper {
    private final OrderProductMapper orderProductMapper;

    /**
     * Maps OrderExtra entity to OrderDTO structure
     * @param order OrderExtra entity
     * @return OrderDTO
     */
    public OrderDTO toOrderDTO(OrderExtra order) {
        OrderDTO dto = new OrderDTO();
        dto.setId(order.getId());
        dto.setCreatedAt(order.getCreatedAt());
        dto.setPaid(order.isPaid());

        List<ProductQuantityDTO> productList = order.getItems().stream()
                .map(orderProductMapper::toOrderProductDTO)
                .collect(Collectors.toList());

        dto.setItems(productList);
        return dto;
    }

    /**
     * Maps CreateOrderDTO structure to Order entity
     * @param dto CreateOrderDTO
     * @return Order entity
     */
    public Order toOrder(CreateOrderDTO dto) {
        Order order = new Order();
        order.setPaid(dto.getPaid() != null ? dto.getPaid() : false);
        return order;
    }
}