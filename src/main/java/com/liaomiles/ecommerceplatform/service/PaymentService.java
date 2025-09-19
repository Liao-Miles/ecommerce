package com.liaomiles.ecommerceplatform.service;

import com.liaomiles.ecommerceplatform.dto.response.PaymentResponse;
import com.liaomiles.ecommerceplatform.dto.request.PaymentRequest;
import com.liaomiles.ecommerceplatform.entity.Order;
import com.liaomiles.ecommerceplatform.entity.Payment;
import com.liaomiles.ecommerceplatform.repository.OrderRepository;
import com.liaomiles.ecommerceplatform.repository.PaymentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    public PaymentService(PaymentRepository paymentRepository, OrderRepository orderRepository) {
        this.paymentRepository = paymentRepository;
        this.orderRepository = orderRepository;
    }

    @Transactional
    public PaymentResponse processPayment(PaymentRequest req) {
        Order order = orderRepository.findById(req.getOrderId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));

        Payment payment = new Payment();
        payment.setOrder(order);
        payment.setPaymentMethod(req.getMethod());
        payment.setAmount(BigDecimal.valueOf(req.getAmount()));
        payment.setStatus("success");

        Payment saved = paymentRepository.save(payment);

        order.setStatus("paid");
        orderRepository.save(order);

        return new PaymentResponse(
                saved.getId(),
                order.getId(),
                saved.getPaymentMethod(),
                saved.getAmount().doubleValue(),
                saved.getStatus()
        );
    }
}

