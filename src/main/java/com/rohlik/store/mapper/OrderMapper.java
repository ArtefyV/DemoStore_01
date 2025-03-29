package com.rohlik.store.mapper;

import com.rohlik.store.dto.CreateOrderDTO;
import com.rohlik.store.dto.OrderDTO;
import com.rohlik.store.dto.ProductQuantityDTO;
import com.rohlik.store.dto.UpdateOrderDTO;
import com.rohlik.store.model.Order;
import com.rohlik.store.model.OrderExtra;
import com.rohlik.store.model.OrderProduct;
import com.rohlik.store.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderMapper {
    private final OrderProductMapper orderProductMapper;

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


    public Order toOrder(CreateOrderDTO dto) {
        Order order = new Order();
        if (dto.getPaid() != null) {
            order.setPaid(dto.getPaid());
        }
        return order;
    }

/*    public Order toOrder(UpdateOrderDTO dto) {
        Order order = new Order();
        order.setId(dto.getId());
        if (dto.getPaid() != null) {
            order.setPaid(dto.getPaid());
        }

        if (dto.getItems() != null) {
            List<OrderProduct> updatedItems = dto.getItems().stream()
                    .map(orderProductMapper::toOrderProduct)
                    .collect(Collectors.toList());

            order.setItems(updatedItems);
        }
        return order;
    }*/
}