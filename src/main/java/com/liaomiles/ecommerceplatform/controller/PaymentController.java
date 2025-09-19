package com.liaomiles.ecommerceplatform.controller;

import com.liaomiles.ecommerceplatform.dto.response.PaymentResponse;
import com.liaomiles.ecommerceplatform.dto.request.PaymentRequest;
import com.liaomiles.ecommerceplatform.service.PaymentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping
    public ResponseEntity<PaymentResponse> create(@Valid @RequestBody PaymentRequest request) {
        PaymentResponse resp = paymentService.processPayment(request);
        return ResponseEntity.created(URI.create("/payments/" + resp.getId())).body(resp);
    }
}

