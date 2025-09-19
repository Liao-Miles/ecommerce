package com.liaomiles.ecommerceplatform.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class CreateOrderRequest {
    @NotNull
    private Long userId;

    // 直接下單時使用 - 商品清單
    @Valid
    private List<OrderItemRequest> items;

    // 配送地址 (可選)
    private String shippingAddress;

    @Data
    public static class OrderItemRequest {
        @NotNull
        private Long productId;

        @NotNull
        private Integer quantity;
    }
}
