package com.liaomiles.ecommerceplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liaomiles.ecommerceplatform.dto.response.OrderItemResponse;
import com.liaomiles.ecommerceplatform.dto.response.OrderResponse;
import com.liaomiles.ecommerceplatform.service.OrderService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(OrderControllerTest.Config.class)
class OrderControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        OrderService orderService() {
            return mock(OrderService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private OrderService orderService;

    @Test
    @DisplayName("POST /orders 從購物車建立訂單 → 201 Created + Location")
    void createOrder_created() throws Exception {
        List<OrderItemResponse> items = List.of(
                new OrderItemResponse(11L, 100L, "P1", new BigDecimal("9.99"), 2, new BigDecimal("19.98"))
        );
        OrderResponse resp = new OrderResponse(5L, 10L, "pending", new BigDecimal("19.98"), LocalDateTime.now(), items);
        given(orderService.createOrderFromCart(any())).willReturn(resp);

        String json = "{\"userId\":10}";

        mockMvc.perform(post("/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/orders/5"))
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.items[0].productName").value("P1"));
    }

    @Test
    @DisplayName("GET /orders/{id} → 200 OK 單筆訂單")
    void getOrder_ok() throws Exception {
        List<OrderItemResponse> items = List.of(
                new OrderItemResponse(11L, 100L, "P1", new BigDecimal("9.99"), 2, new BigDecimal("19.98"))
        );
        OrderResponse resp = new OrderResponse(5L, 10L, "pending", new BigDecimal("19.98"), LocalDateTime.now(), items);
        given(orderService.getOrder(anyLong())).willReturn(resp);

        mockMvc.perform(get("/orders/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userId").value(10))
                .andExpect(jsonPath("$.items[0].quantity").value(2));
    }

    @Test
    @DisplayName("GET /orders?userId=10 → 200 OK 訂單列表")
    void listOrders_ok() throws Exception {
        List<OrderResponse> list = List.of(
                new OrderResponse(5L, 10L, "pending", new BigDecimal("19.98"), LocalDateTime.now(), List.of())
        );
        given(orderService.listOrders(anyLong())).willReturn(list);

        mockMvc.perform(get("/orders").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(5));
    }
}
