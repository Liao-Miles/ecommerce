package com.liaomiles.ecommerceplatform.controller;

import com.liaomiles.ecommerceplatform.dto.request.CreateOrderRequest;
import com.liaomiles.ecommerceplatform.dto.response.OrderResponse;
import com.liaomiles.ecommerceplatform.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 從購物車下單
    @PostMapping("/from-cart")
    public ResponseEntity<OrderResponse> createFromCart(@Valid @RequestBody CreateOrderRequest request) {
        // 確保這是從購物車下單（不應該有 items）
        if (request.getItems() != null && !request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Use /orders/direct for direct ordering");
        }
        OrderResponse resp = orderService.createOrderFromCart(request);
        return ResponseEntity.created(URI.create("/orders/" + resp.getId())).body(resp);
    }

    // 直接下單
    @PostMapping("/direct")
    public ResponseEntity<OrderResponse> createDirect(@Valid @RequestBody CreateOrderRequest request) {
        // 確保有商品清單
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new IllegalArgumentException("Items are required for direct ordering");
        }
        OrderResponse resp = orderService.createDirectOrder(request);
        return ResponseEntity.created(URI.create("/orders/" + resp.getId())).body(resp);
    }

    // 查詢用戶的所有訂單/orders?userId=xxx
    @GetMapping
    public List<OrderResponse> list(@RequestParam("userId") Long userId) {
        return orderService.listOrders(userId);
    }

    // 查詢單一訂單 - 放在最後，避免路由衝突
    @GetMapping("/{id}")
    public OrderResponse getOne(@PathVariable("id") Long id) {
        return orderService.getOrder(id);
    }
}
