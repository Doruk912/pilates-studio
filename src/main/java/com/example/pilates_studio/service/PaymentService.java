package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.PaymentDto;
import com.example.pilates_studio.model.Payment;
import jakarta.validation.Valid;

import java.util.List;

public interface PaymentService {
    List<PaymentDto> getAllPayments();

    void savePayment(@Valid PaymentDto payment);

    List<Payment> findPaymentsByPurchaseId(Integer purchaseId);
}
