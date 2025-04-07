package com.demo.store.controller;

import com.demo.store.dto.CreateOrderDTO;
import com.demo.store.dto.OrderDTO;
import com.demo.store.mapper.OrderMapper;
import com.demo.store.mapper.OrderProductMapper;
import com.demo.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operations with orders")
public class OrderController {

    private final OrderService orderService;
    private final OrderMapper orderMapper;
    private final OrderProductMapper orderProductMapper;

    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders found")
    })
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders()
                .stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    @Operation(summary = "Get order by ID", description = "Returns the order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<OrderDTO> getOrderById(@PathVariable Long id) {
        OrderDTO order = orderMapper.toOrderDTO(orderService.getOrderById(id));
        return ResponseEntity.ok(order);
    }

    @Operation(summary = "Create order", description = "Creates a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Order created")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<OrderDTO> createOrder(@Valid @RequestBody CreateOrderDTO dto) {
        OrderDTO createdOrder = orderMapper.toOrderDTO(orderService.createOrder(orderMapper.toOrder(dto), orderProductMapper.toOrderProducts(dto)));
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOrder);
    }

    @Operation(summary = "Update status of order", description = "Updates status of an existing order to paid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order status updated"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PutMapping("/{id}/pay")
    public ResponseEntity<OrderDTO> payOrder(@PathVariable Long id) {
        OrderDTO updatedOrder = orderMapper.toOrderDTO(orderService.payOrder(id));
        return ResponseEntity.ok(updatedOrder);
    }

    @Operation(summary = "Cancel order", description = "Cancels an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order canceled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
    }

    @Operation(summary = "Get orders by paid status", description = "Returns a list of orders by paid status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders found")
    })
    @GetMapping("/status")
    public ResponseEntity<List<OrderDTO>> getOrdersByPaidStatus(@RequestParam boolean paid) {
        List<OrderDTO> orders = orderService.getOrdersByPaidStatus(paid)
                .stream()
                .map(orderMapper::toOrderDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }
}
