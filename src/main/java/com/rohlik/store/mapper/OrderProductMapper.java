package com.rohlik.store.mapper;

import com.rohlik.store.dto.CreateOrderDTO;
import com.rohlik.store.dto.ProductIdQuantityDTO;
import com.rohlik.store.dto.ProductQuantityDTO;
import com.rohlik.store.model.Order;
import com.rohlik.store.model.OrderProduct;
import com.rohlik.store.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OrderProductMapper {
    private final ProductMapper productMapper;

    public OrderProductMapper(ProductMapper productMapper) {
        this.productMapper = productMapper;
    }

    public ProductQuantityDTO toOrderProductDTO(OrderProduct orderProduct) {
        ProductQuantityDTO dto = new ProductQuantityDTO();
        dto.setQuantity(orderProduct.getQuantity());

        dto.setProduct(productMapper.toProductDTO(orderProduct.getProduct()));

        return dto;
    }

    public OrderProduct toOrderProduct(ProductQuantityDTO dto) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(dto.getQuantity());
        orderProduct.setProduct(productMapper.toProduct(dto.getProduct()));
        return orderProduct;
    }

    public OrderProduct toOrderProduct(ProductIdQuantityDTO dto) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(dto.getQuantity());
        Product product = new Product();
        product.setId(dto.getProductId());
        orderProduct.setProduct(product);
        return orderProduct;
    }

    public List<OrderProduct> toOrderProducts(CreateOrderDTO dto) {
        return dto.getItems().stream()
                .map(this::toOrderProduct)
                .collect(Collectors.toList());
    }
}