package com.rohlik.store.mapper;

import com.rohlik.store.dto.CreateOrderDTO;
import com.rohlik.store.dto.ProductIdQuantityDTO;
import com.rohlik.store.dto.ProductQuantityDTO;
import com.rohlik.store.model.OrderProduct;
import com.rohlik.store.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderProductMapper {
    private final ProductMapper productMapper;

    /**
     * Maps OrderProduct entity to ProductQuantityDTO structure
     * @param orderProduct OrderProduct entity
     * @return ProductQuantityDTO
     */
    public ProductQuantityDTO toOrderProductDTO(OrderProduct orderProduct) {
        ProductQuantityDTO dto = new ProductQuantityDTO();
        dto.setQuantity(orderProduct.getQuantity());
        dto.setProduct(productMapper.toProductDTO(orderProduct.getProduct()));
        return dto;
    }
/*

    public OrderProduct toOrderProduct(ProductQuantityDTO dto) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(dto.getQuantity());
        orderProduct.setProduct(productMapper.toProduct(dto.getProduct()));
        return orderProduct;
    }
*/

    /**
     * Maps ProductIdQuantityDTO structure to OrderProduct entity
     * @param dto ProductIdQuantityDTO
     * @return OrderProduct entity
     */
    public OrderProduct toOrderProduct(ProductIdQuantityDTO dto) {
        OrderProduct orderProduct = new OrderProduct();
        orderProduct.setQuantity(dto.getQuantity());
        Product product = new Product();
        product.setId(dto.getProductId());
        orderProduct.setProduct(product);
        return orderProduct;
    }

    /**
     * Maps List of ProductIdQuantityDTO structure incoming with parameter 'dto' to List of OrderProduct entities
     * @param dto CreateOrderDTO
     * @return List of OrderProduct entities
     */
    public List<OrderProduct> toOrderProducts(CreateOrderDTO dto) {
        return dto.getItems().stream()
                .map(this::toOrderProduct)
                .collect(Collectors.toList());
    }
}