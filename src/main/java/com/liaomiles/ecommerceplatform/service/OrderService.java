package com.liaomiles.ecommerceplatform.service;

import com.liaomiles.ecommerceplatform.dto.request.OrderRequest;
import com.liaomiles.ecommerceplatform.dto.response.OrderResponse;
import com.liaomiles.ecommerceplatform.entity.Order;
import com.liaomiles.ecommerceplatform.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;

    public OrderResponse createOrder(OrderRequest request) {
        Order order = new Order();
        order.setUserId(request.userId);
        order.setTotalAmount(request.totalAmount);
        order.setShippingName(request.shippingName);
        order.setShippingPhone(request.shippingPhone);
        order.setShippingAddress(request.shippingAddress);
        order.setShippingNote(request.shippingNote);
        order.setShippingMethod(request.shippingMethod);
        // 預設值
        order.setStatus("pending");
        order.setShippingStatus("pending");
        order = orderRepository.save(order);
        return toResponse(order);
    }

    public List<OrderResponse> getOrdersByUserId(Long userId) {
        return orderRepository.findByUserId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        return orderRepository.findById(id).map(this::toResponse).orElse(null);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse resp = new OrderResponse();
        resp.id = order.getId();
        resp.userId = order.getUserId();
        resp.status = order.getStatus();
        resp.totalAmount = order.getTotalAmount();
        resp.createdAt = order.getCreatedAt();
        resp.shippingName = order.getShippingName();
        resp.shippingPhone = order.getShippingPhone();
        resp.shippingAddress = order.getShippingAddress();
        resp.shippingNote = order.getShippingNote();
        resp.shippingMethod = order.getShippingMethod();
        resp.shippingStatus = order.getShippingStatus();
        return resp;
    }
}
