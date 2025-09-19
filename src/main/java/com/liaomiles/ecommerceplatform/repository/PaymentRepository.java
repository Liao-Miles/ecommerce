package com.liaomiles.ecommerceplatform.repository;

import com.liaomiles.ecommerceplatform.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
}

