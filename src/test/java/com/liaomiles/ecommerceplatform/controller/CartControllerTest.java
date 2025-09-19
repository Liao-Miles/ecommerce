package com.liaomiles.ecommerceplatform.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.liaomiles.ecommerceplatform.dto.request.AddCartItemRequest;
import com.liaomiles.ecommerceplatform.dto.response.CartItemResponse;
import com.liaomiles.ecommerceplatform.service.CartService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CartController.class)
@Import(CartControllerTest.Config.class)
class CartControllerTest {

    @TestConfiguration
    static class Config {
        @Bean
        CartService cartService() {
            return mock(CartService.class);
        }
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CartService cartService;

    @Test
    @DisplayName("POST /cart should create and return 201 with body")
    void addCartItem_created() throws Exception {
        CartItemResponse resp = new CartItemResponse(1L, 10L, 100L, "P1", new BigDecimal("9.99"), 2, new BigDecimal("19.98"));
        given(cartService.addToCart(any())).willReturn(resp);

        AddCartItemRequest req = new AddCartItemRequest();
        req.setUserId(10L);
        req.setProductId(100L);
        req.setQuantity(2);

        mockMvc.perform(post("/cart")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/cart/1"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("GET /cart?userId=10 should return list")
    void listCart_ok() throws Exception {
        List<CartItemResponse> list = List.of(
                new CartItemResponse(1L, 10L, 100L, "P1", new BigDecimal("9.99"), 2, new BigDecimal("19.98"))
        );
        given(cartService.getCart(anyLong())).willReturn(list);

        mockMvc.perform(get("/cart").param("userId", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(10));
    }

    @Test
    @DisplayName("DELETE /cart/1 should return 204")
    void deleteCartItem_noContent() throws Exception {
        mockMvc.perform(delete("/cart/1"))
                .andExpect(status().isNoContent());
    }
}
