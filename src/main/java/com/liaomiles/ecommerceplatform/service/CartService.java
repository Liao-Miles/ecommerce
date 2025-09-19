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
import java.util.UUID;
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
        User user = null;
        if (req.getUserId() != null) {
            user = userRepository.findById(req.getUserId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        }
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        CartItem item;
        String sessionId = req.getSessionId();
        if (user != null) {
            final User finalUser = user;
            item = cartItemRepository.findByUser_IdAndProduct_Id(finalUser.getId(), product.getId())
                    .map(existing -> {
                        existing.setQuantity(existing.getQuantity() + req.getQuantity());
                        return existing;
                    })
                    .orElseGet(() -> {
                        CartItem ci = new CartItem();
                        ci.setUser(finalUser);
                        ci.setProduct(product);
                        ci.setQuantity(req.getQuantity());
                        return ci;
                    });
        } else {
            // 未登入，sessionId 若為 null 則自動產生
            if (sessionId == null || sessionId.isEmpty()) {
                sessionId = UUID.randomUUID().toString();
            }
            final String finalSessionId = sessionId;
            item = cartItemRepository.findBySessionIdAndProduct_Id(finalSessionId, product.getId())
                    .map(existing -> {
                        existing.setQuantity(existing.getQuantity() + req.getQuantity());
                        return existing;
                    })
                    .orElseGet(() -> {
                        CartItem ci = new CartItem();
                        ci.setSessionId(finalSessionId);
                        ci.setProduct(product);
                        ci.setQuantity(req.getQuantity());
                        return ci;
                    });
        }
        CartItem saved = cartItemRepository.save(item);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<CartItemResponse> getCart(Long userId, String sessionId) {
        if (userId != null) {
            userRepository.findById(userId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
            return cartItemRepository.findAllByUser_Id(userId).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } else if (sessionId != null) {
            return cartItemRepository.findAllBySessionId(sessionId).stream()
                    .map(this::toResponse)
                    .collect(Collectors.toList());
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "userId or sessionId is required");
        }
    }

    @Transactional
    public void mergeCartOnLogin(Long userId, String sessionId) {
        if (userId == null || sessionId == null) return;
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        List<CartItem> sessionCart = cartItemRepository.findAllBySessionId(sessionId);
        for (CartItem sessionItem : sessionCart) {
            CartItem userItem = cartItemRepository.findByUser_IdAndProduct_Id(userId, sessionItem.getProduct().getId())
                    .orElse(null);
            if (userItem != null) {
                userItem.setQuantity(userItem.getQuantity() + sessionItem.getQuantity());
                cartItemRepository.save(userItem);
                cartItemRepository.deleteById(sessionItem.getId());
            } else {
                sessionItem.setUser(user);
                sessionItem.setSessionId(null);
                cartItemRepository.save(sessionItem);
            }
        }
        cartItemRepository.deleteAllBySessionId(sessionId);
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
        Long userId = item.getUser() != null ? item.getUser().getId() : null;
        String sessionId = item.getSessionId();
        return new CartItemResponse(
                item.getId(),
                userId,
                p.getId(),
                p.getName(),
                price,
                item.getQuantity(),
                subtotal,
                sessionId
        );
    }
}
