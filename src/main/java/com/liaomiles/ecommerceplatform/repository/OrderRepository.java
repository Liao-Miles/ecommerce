package com.liaomiles.ecommerceplatform.repository;

import com.liaomiles.ecommerceplatform.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByUser_IdOrderByCreatedAtDesc(Long userId);
}

