package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.PurchaseDto;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.model.PackageStatus;
import com.example.pilates_studio.model.Purchase;
import com.example.pilates_studio.repository.CustomerRepository;
import com.example.pilates_studio.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PurchaseServiceImpl implements PurchaseService{

    private PurchaseRepository purchaseRepository;
    private CustomerRepository customerRepository;
    private CustomerService customerService;

    @Autowired
    public PurchaseServiceImpl(PurchaseRepository purchaseRepository, CustomerRepository customerRepository, CustomerService customerService){
        this.purchaseRepository = purchaseRepository;
        this.customerRepository = customerRepository;
        this.customerService = customerService;
    }

    public List<PurchaseDto> findAllPurchases(){
        return purchaseRepository.findAllPurchasesWithCustomerNames()
                .stream()
                .map(this::mapToPurchaseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Purchase savePurchase(PurchaseDto purchaseDto) {
        Customer customer = customerService.findCustomerByName(purchaseDto.getCustomerName());
        Purchase purchase = mapToPurchase(purchaseDto);
        return purchaseRepository.save(purchase);
    }

    @Override
    public boolean existsInUsePurchaseByCustomerId(Integer customerId) {
        return purchaseRepository.existsByCustomer_IdAndPackageStatus(customerId, PackageStatus.IN_USE);
    }

    @Override
    public PurchaseDto findPurchaseById(Integer purchaseId) {
        Purchase purchase = purchaseRepository.findById(purchaseId).get();
        return mapToPurchaseDto(purchase);
    }

    @Override
    public void updatePurchase(PurchaseDto purchaseDto) {
        Purchase purchase = mapToPurchase(purchaseDto);
        purchaseRepository.save(purchase);
    }

    @Transactional
    @Override
    public void setStatusAsUsed(Customer customer) {
        Purchase purchase = purchaseRepository.findByCustomerIdAndPackageStatus(customer.getId(), PackageStatus.IN_USE);
        purchase.setPackageStatus(PackageStatus.USED);
        purchaseRepository.save(purchase);
    }

    @Override
    public int countPurchasesInUse() {
        return purchaseRepository.countByPackageStatus(PackageStatus.IN_USE);
    }

    @Override
    public Integer findInUsePurchaseIdByCustomerId(Integer id) {
        Purchase purchase = purchaseRepository.findByCustomerIdAndPackageStatus(id, PackageStatus.IN_USE);
        if(purchase != null)
            return purchase.getId();
        return null;
    }

    @Override
    public boolean existsByPurchaseId(Integer purchaseId) {
        return purchaseRepository.existsById(purchaseId);
    }

    @Override
    public Purchase findUnpaidPurchaseIdByCustomerName(String customerName) {
        Customer customer = customerRepository.findByName(customerName);
        if(customer != null){
            Purchase purchase = purchaseRepository.findFirstByCustomer_IdAndPaymentCompleteFalse(customer.getId());
            return purchase;
        }
        System.out.println("Customer not found");
        return null;
    }


    public PurchaseDto mapToPurchaseDto(Purchase purchase){
        return PurchaseDto.builder()
                .id(purchase.getId())
                .customerName(purchase.getCustomer().getName())
                .paymentComplete(purchase.isPaymentComplete())
                .packageStatus(purchase.getPackageStatus())
                .description(purchase.getDescription())
                .lessonCount(purchase.getLessonCount())
                .price(purchase.getPrice())
                .amountDue(purchase.getAmountDue())
                .build();
    }

    public Purchase mapToPurchase(PurchaseDto purchaseDto){
        Customer customer = customerService.findCustomerByName(purchaseDto.getCustomerName());
        return Purchase.builder()
                .id(purchaseDto.getId())
                .customer(customer)
                .paymentComplete(purchaseDto.getPaymentComplete())
                .packageStatus(purchaseDto.getPackageStatus())
                .description(purchaseDto.getDescription())
                .lessonCount(purchaseDto.getLessonCount())
                .price(purchaseDto.getPrice())
                .amountDue(purchaseDto.getAmountDue())
                .build();
    }

}
