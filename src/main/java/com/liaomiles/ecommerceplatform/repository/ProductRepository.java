package com.liaomiles.ecommerceplatform.repository;

import com.liaomiles.ecommerceplatform.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByCategory_NameIgnoreCase(String categoryName);
}

