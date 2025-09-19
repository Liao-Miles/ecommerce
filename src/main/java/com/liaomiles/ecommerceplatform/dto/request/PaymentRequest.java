package com.liaomiles.ecommerceplatform.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class PaymentRequest {
    @NotNull
    private Long orderId;

    @NotBlank
    private String method;

    @NotNull
    @Positive
    private Double amount;
}
