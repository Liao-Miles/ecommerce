package com.liaomiles.ecommerceplatform.service;

import com.liaomiles.ecommerceplatform.dto.response.PaymentResponse;
import com.liaomiles.ecommerceplatform.dto.request.PaymentRequest;
import com.liaomiles.ecommerceplatform.entity.Order;
import com.liaomiles.ecommerceplatform.entity.OrderStatus;
import com.liaomiles.ecommerceplatform.entity.Payment;
import com.liaomiles.ecommerceplatform.entity.PaymentStatus;
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

        // 金額改從訂單抓
        payment.setAmount(order.getTotalAmount());

        // 簡單邏輯：這裡直接設成功即可（沒有金流）
        if (order.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            payment.setStatus(PaymentStatus.FAILED);
            order.setStatus(OrderStatus.CANCELLED);
        } else {
            payment.setStatus(PaymentStatus.SUCCESS);
            order.setStatus(OrderStatus.PAID);
        }

        Payment saved = paymentRepository.save(payment);
        orderRepository.save(order);

        return new PaymentResponse(
                saved.getId(),
                order.getId(),
                saved.getPaymentMethod(),
                saved.getAmount().doubleValue(),
                saved.getStatus()
        );
    }

    // 模擬退款
    @Transactional
    public PaymentResponse refundPayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Payment not found"));

        if (payment.getStatus() != PaymentStatus.SUCCESS) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Only successful payments can be refunded");
        }

        payment.setStatus(PaymentStatus.REFUNDED);
        paymentRepository.save(payment);

        Order order = payment.getOrder();
        order.setStatus(OrderStatus.CANCELLED);
        orderRepository.save(order);

        return new PaymentResponse(
                payment.getId(),
                order.getId(),
                payment.getPaymentMethod(),
                payment.getAmount().doubleValue(),
                payment.getStatus()
        );
    }
}


