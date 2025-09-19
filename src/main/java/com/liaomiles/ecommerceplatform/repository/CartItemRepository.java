package com.liaomiles.ecommerceplatform.repository;

import com.liaomiles.ecommerceplatform.entity.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByUser_Id(Long userId);
    Optional<CartItem> findByUser_IdAndProduct_Id(Long userId, Long productId);
    void deleteAllByUser_Id(Long userId);
}
