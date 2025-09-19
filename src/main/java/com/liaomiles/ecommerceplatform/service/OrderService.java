package com.liaomiles.ecommerceplatform.service;

import com.liaomiles.ecommerceplatform.dto.request.CreateOrderRequest;
import com.liaomiles.ecommerceplatform.dto.response.OrderItemResponse;
import com.liaomiles.ecommerceplatform.dto.response.OrderResponse;
import com.liaomiles.ecommerceplatform.entity.*;
import com.liaomiles.ecommerceplatform.repository.CartItemRepository;
import com.liaomiles.ecommerceplatform.repository.OrderItemRepository;
import com.liaomiles.ecommerceplatform.repository.OrderRepository;
import com.liaomiles.ecommerceplatform.repository.ProductRepository;
import com.liaomiles.ecommerceplatform.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository,
                        OrderItemRepository orderItemRepository,
                        CartItemRepository cartItemRepository,
                        ProductRepository productRepository,
                        UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public OrderResponse createOrderFromCart(CreateOrderRequest req) {
        if (req == null || req.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }

        // 如果有直接商品清單，就直接下單
        if (req.getItems() != null && !req.getItems().isEmpty()) {
            return createDirectOrder(req);
        }

        // 否則從購物車下單
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<CartItem> cartItems = cartItemRepository.findAllByUser_Id(user.getId());
        if (cartItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Cart is empty");
        }

        // 计算总价
        BigDecimal total = cartItems.stream()
                .map(ci -> ci.getProduct().getPrice().multiply(BigDecimal.valueOf(ci.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 创建订单
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // 创建订单项
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem ci : cartItems) {
            Product p = ci.getProduct();
            OrderItem oi = new OrderItem();
            oi.setOrder(savedOrder);
            oi.setProduct(p);
            oi.setQuantity(ci.getQuantity());
            oi.setPrice(p.getPrice());
            orderItems.add(oi);
        }
        orderItemRepository.saveAll(orderItems);

        // 清空购物车
        cartItemRepository.deleteAllByUser_Id(user.getId());

        return toResponse(savedOrder, orderItems);
    }

    @Transactional
    public OrderResponse createDirectOrder(CreateOrderRequest req) {
        if (req == null || req.getUserId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        if (req.getItems() == null || req.getItems().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Items are required for direct order");
        }

        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        // 驗證商品並計算總價
        List<OrderItem> orderItems = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (CreateOrderRequest.OrderItemRequest itemReq : req.getItems()) {
            Product product = productRepository.findById(itemReq.getProductId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                            "Product not found: " + itemReq.getProductId()));

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemReq.getQuantity()));
            total = total.add(itemTotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setProduct(product);
            orderItem.setQuantity(itemReq.getQuantity());
            orderItem.setPrice(product.getPrice());
            orderItems.add(orderItem);
        }

        // 創建訂單
        Order order = new Order();
        order.setUser(user);
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(total);
        Order savedOrder = orderRepository.save(order);

        // 設定訂單關聯並保存訂單項目
        orderItems.forEach(oi -> oi.setOrder(savedOrder));
        orderItemRepository.saveAll(orderItems);

        return toResponse(savedOrder, orderItems);
    }

    @Transactional(readOnly = true)
    public OrderResponse getOrder(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found"));
        List<OrderItem> items = orderItemRepository.findAllByOrder_Id(order.getId());
        return toResponse(order, items);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> listOrders(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        // 确保用户存在（可选）
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        return orderRepository.findAllByUser_IdOrderByCreatedAtDesc(userId).stream()
                .map(o -> toResponse(o, orderItemRepository.findAllByOrder_Id(o.getId())))
                .collect(Collectors.toList());
    }

    private OrderResponse toResponse(Order order, List<OrderItem> items) {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(oi -> new OrderItemResponse(
                        oi.getId(),
                        oi.getProduct().getId(),
                        oi.getProduct().getName(),
                        oi.getPrice(),
                        oi.getQuantity(),
                        oi.getPrice().multiply(BigDecimal.valueOf(oi.getQuantity()))
                ))
                .collect(Collectors.toList());
        return new OrderResponse(
                order.getId(),
                order.getUser().getId(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                itemResponses
        );
    }
}

