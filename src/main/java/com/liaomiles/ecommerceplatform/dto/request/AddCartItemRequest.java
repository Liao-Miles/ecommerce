package com.liaomiles.ecommerceplatform.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AddCartItemRequest {
    private Long userId; // 已登入用戶可填
    private String sessionId; // 未登入訪客可填

    @NotNull
    private Long productId;

    @NotNull
    @Min(1)
    private Integer quantity;
}
