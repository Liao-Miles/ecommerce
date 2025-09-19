package com.liaomiles.ecommerceplatform.controller;

import com.liaomiles.ecommerceplatform.dto.request.AddCartItemRequest;
import com.liaomiles.ecommerceplatform.dto.response.CartItemResponse;
import com.liaomiles.ecommerceplatform.service.CartService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartItemResponse> add(@Valid @RequestBody AddCartItemRequest request) {
        // request 內 userId、sessionId 擇一必填
        CartItemResponse resp = cartService.addToCart(request);
        return ResponseEntity.created(URI.create("/cart/" + resp.getId())).body(resp);
    }

    @GetMapping
    public List<CartItemResponse> list(@RequestParam(value = "userId", required = false) Long userId,
                                       @RequestParam(value = "sessionId", required = false) String sessionId) {
        // userId、sessionId 擇一必填
        return cartService.getCart(userId, sessionId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        cartService.removeItem(id);
        return ResponseEntity.noContent().build();
    }
}
