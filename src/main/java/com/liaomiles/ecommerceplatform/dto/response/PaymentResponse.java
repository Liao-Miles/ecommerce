package com.liaomiles.ecommerceplatform.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PaymentResponse {
    private Long id;
    private Long orderId;
    private String method;
    private Double amount;
    private String status;
}
