package com.liaomiles.ecommerceplatform.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "cart_items",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_product", columnNames = {"user_id", "product_id"}),
        @UniqueConstraint(name = "uk_session_product", columnNames = {"session_id", "product_id"})
    }
)
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // 可為 null，未登入時為 null

    @Column(name = "session_id")
    private String sessionId; // 未登入時用 sessionId

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer quantity;

}
