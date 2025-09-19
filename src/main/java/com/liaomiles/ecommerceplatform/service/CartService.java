package com.liaomiles.ecommerceplatform.service;

import com.liaomiles.ecommerceplatform.dto.request.AddCartItemRequest;
import com.liaomiles.ecommerceplatform.dto.response.CartItemResponse;
import com.liaomiles.ecommerceplatform.entity.CartItem;
import com.liaomiles.ecommerceplatform.entity.Product;
import com.liaomiles.ecommerceplatform.entity.User;
import com.liaomiles.ecommerceplatform.repository.CartItemRepository;
import com.liaomiles.ecommerceplatform.repository.ProductRepository;
import com.liaomiles.ecommerceplatform.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository,
                       UserRepository userRepository,
                       ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public CartItemResponse addToCart(AddCartItemRequest req) {
        User user = userRepository.findById(req.getUserId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CartItem item = cartItemRepository.findByUser_IdAndProduct_Id(user.getId(), product.getId())
                .map(existing -> {
                    existing.setQuantity(existing.getQuantity() + req.getQuantity());
                    return existing;
                })
                .orElseGet(() -> {
                    CartItem ci = new CartItem();
                    ci.setUser(user);
                    ci.setProduct(product);
                    ci.setQuantity(req.getQuantity());
                    return ci;
                });

        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId) {
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId is required");
        }
        // 確保用戶存在
        userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        return cartItemRepository.findAllByUser_Id(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void removeItem(Long id) {
        if (!cartItemRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Cart item not found");
        }
        cartItemRepository.deleteById(id);
    }

    private CartItemResponse toResponse(CartItem item) {
        Product p = item.getProduct();
        BigDecimal price = p.getPrice();
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(item.getQuantity()));
        return new CartItemResponse(
                item.getId(),
                item.getUser().getId(),
                p.getId(),
                p.getName(),
                price,
                item.getQuantity(),
                subtotal
        );
    }
}

