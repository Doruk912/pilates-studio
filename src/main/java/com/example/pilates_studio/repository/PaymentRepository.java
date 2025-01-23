package com.example.pilates_studio.repository;

import com.example.pilates_studio.dto.PaymentDto;
import com.example.pilates_studio.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Integer> {
    List<Payment> findPaymentsByPurchaseId(Integer purchaseId);
}
