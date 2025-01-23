package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.PurchaseDto;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.model.Purchase;

import java.util.List;

public interface PurchaseService {
    List<PurchaseDto> findAllPurchases();

    Purchase savePurchase(PurchaseDto purchaseDto);

    boolean existsInUsePurchaseByCustomerId(Integer CustomerId);

    PurchaseDto findPurchaseById(Integer purchaseId);

    void updatePurchase(PurchaseDto purchaseDto);

    void setStatusAsUsed(Customer customer);

    int countPurchasesInUse();

    Integer findInUsePurchaseIdByCustomerId(Integer id);

    boolean existsByPurchaseId(Integer purchaseId);

    Purchase findUnpaidPurchaseIdByCustomerName(String customerName);

    PurchaseDto mapToPurchaseDto(Purchase purchase);

    Purchase mapToPurchase(PurchaseDto purchaseDto);
}
