package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.model.PackageStatus;
import com.example.pilates_studio.model.Purchase;
import com.example.pilates_studio.repository.CustomerRepository;
import com.example.pilates_studio.repository.PurchaseRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerServiceImpl implements CustomerService{
    private CustomerRepository customerRepository;
    private PurchaseRepository purchaseRepository;

    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository, PurchaseRepository purchaseRepository){
        this.customerRepository = customerRepository;
        this.purchaseRepository = purchaseRepository;
    }


    @Override
    public List<CustomerDto> findAllCustomers() {
        List<Customer> customers = customerRepository.findAll();
        return customers.stream().sorted(Comparator.comparing(Customer::getName)).map((customer) -> mapToCustomerDto(customer)).collect(Collectors.toList());
    }

    @Override
    public Customer saveCustomer(CustomerDto customerDto) {
        Customer customer = mapToCustomer(customerDto);
        return customerRepository.save(customer);
    }

    @Override
    public CustomerDto findCustomerById(Integer customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        return mapToCustomerDto(customer);
    }

    public Customer findCustomerModelById(Integer customerId){
        return customerRepository.findById(customerId).get();
    }

    @Override
    public int countTotalCustomers() {
        return ((int) customerRepository.count());
    }

    @Override
    public void updateCustomer(CustomerDto customerDto) {
        Customer customer = mapToCustomer(customerDto);
        customerRepository.save(customer);
        if(customer.getRemainingUsage() == 0){
            Purchase purchase = purchaseRepository.findByCustomerIdAndPackageStatus(customer.getId(), PackageStatus.IN_USE);
            purchase.setPackageStatus(PackageStatus.USED);
            purchaseRepository.save(purchase);
        }
    }

    @Override
    public void delete(Integer customerId) {
        customerRepository.deleteById(customerId);
    }

    private Customer mapToCustomer(CustomerDto customer){
        return Customer.builder()
                .Id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .remainingUsage(customer.getRemainingUsage())
                .build();
    }

    private CustomerDto mapToCustomerDto(Customer customer){
        return CustomerDto.builder()
                .Id(customer.getId())
                .name(customer.getName())
                .phone(customer.getPhone())
                .remainingUsage(customer.getRemainingUsage())
                .build();
    }

    //To be used in PurchaseService
    public Customer findCustomerByName(String name){
        return customerRepository.findByName(name);
    }

    @Transactional
    @Override
    public void decrementUsage(Integer customerId) {
        Customer customer = customerRepository.findById(customerId).get();
        customer.setRemainingUsage(customer.getRemainingUsage() - 1);
        customerRepository.save(customer);
    }

}
