package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.PaymentDto;
import com.example.pilates_studio.model.Payment;
import com.example.pilates_studio.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PaymentServiceImpl implements PaymentService{

    private final PaymentRepository paymentRepository;
    private final PurchaseService purchaseService;

    public PaymentServiceImpl(PaymentRepository paymentRepository, PurchaseService purchaseService){
        this.paymentRepository = paymentRepository;
        this.purchaseService = purchaseService;
    }

    @Override
    public List<PaymentDto> getAllPayments() {
        return paymentRepository.findAll()
                .stream()
                .map(this::mapToPaymentDto)
                .collect(Collectors.toList());
    }

    @Override
    public void savePayment(PaymentDto payment) {
        Payment newPayment = mapToPayment(payment);
        paymentRepository.save(newPayment);
    }

    @Override
    public List<Payment> findPaymentsByPurchaseId(Integer purchaseId) {
        return paymentRepository.findPaymentsByPurchaseId(purchaseId);
    }

    private PaymentDto mapToPaymentDto(Payment payment){
        return PaymentDto.builder()
                .id(payment.getId())
                .purchaseId(payment.getPurchase().getId())
                .customerName(payment.getPurchase().getCustomer().getName())
                .paymentDate(payment.getPaymentDate())
                .amount(payment.getAmount())
                .paymentMethod(payment.getPaymentMethod())
                .totalPaidAmount(payment.getTotalPaidAmount())
                .amountDue(payment.getAmountDue())
                .build();
    }

    private Payment mapToPayment(PaymentDto paymentDto){
        Payment payment = new Payment();
        payment.setId(paymentDto.getId());
        payment.setPurchase(purchaseService.mapToPurchase(purchaseService.findPurchaseById(paymentDto.getPurchaseId())));
        payment.setCustomer(payment.getPurchase().getCustomer());
        payment.setPaymentDate(paymentDto.getPaymentDate());
        payment.setAmount(paymentDto.getAmount());
        payment.setPaymentMethod(paymentDto.getPaymentMethod());
        payment.setTotalPaidAmount(paymentDto.getTotalPaidAmount());
        payment.setAmountDue(paymentDto.getAmountDue());
        return payment;
    }

}
