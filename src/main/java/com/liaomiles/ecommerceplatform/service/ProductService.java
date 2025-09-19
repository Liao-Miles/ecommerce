package com.liaomiles.ecommerceplatform.service;

import com.liaomiles.ecommerceplatform.entity.Product;
import com.liaomiles.ecommerceplatform.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> listProducts(String category) {
        if (category != null && !category.isBlank()) {
            return productRepository.findAllByCategory_NameIgnoreCase(category.trim());
        }
        return productRepository.findAll();
    }

    public Optional<Product> getProduct(Long id) {
        return productRepository.findById(id);
    }
}

