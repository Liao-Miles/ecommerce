package com.liaomiles.ecommerceplatform.controller;

import com.liaomiles.ecommerceplatform.dto.response.ProductResponse;
import com.liaomiles.ecommerceplatform.entity.Product;
import com.liaomiles.ecommerceplatform.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // http://localhost:8080/products?category=Books
    @GetMapping
    public List<ProductResponse> list(@RequestParam(value = "category", required = false) String category) {
        return productService.listProducts(category).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getOne(@PathVariable("id") Long id) {
        return productService.getProduct(id)
                .map(p -> ResponseEntity.ok(toDto(p)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ProductResponse toDto(Product p) {
        return new ProductResponse(p.getId(), p.getName(), p.getPrice(), p.getStock());
    }
}
