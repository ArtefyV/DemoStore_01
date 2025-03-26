package com.rohlik.store.controller;

import com.rohlik.store.model.Order;
import com.rohlik.store.service.OrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
@Tag(name = "Orders", description = "Operations with orders")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Get all orders", description = "Returns a list of all orders")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders found")
    })
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @Operation(summary = "Get order by ID", description = "Returns the order by its ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order found"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }

    @Operation(summary = "Create order", description = "Creates a new order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order created")
    })
    @PostMapping
    public ResponseEntity<Order> createOrder(@Valid @RequestBody Order order) {
        return ResponseEntity.ok(orderService.createOrder(order));
    }

    @Operation(summary = "Update status of order", description = "Updates status of an existing order to paid")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Order updated"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @PostMapping("/{id}/pay")
    public ResponseEntity<Order> payOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.payOrder(id));
    }

    @Operation(summary = "Cancel order", description = "Cancels an existing order")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Order canceled"),
            @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long id) {
        orderService.cancelOrder(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get orders by paid status", description = "Returns a list of orders by paid status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of orders found")
    })
    @GetMapping("/status")
    public ResponseEntity<List<Order>> getOrdersByPaidStatus(@RequestParam boolean paid) {
        return ResponseEntity.ok(orderService.getOrdersByPaidStatus(paid));
    }
}
