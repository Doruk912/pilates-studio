package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.CustomerDto;
import com.example.pilates_studio.model.Customer;

import java.util.List;

public interface CustomerService{
    List<CustomerDto> findAllCustomers();
    Customer saveCustomer(CustomerDto customerDto);

    CustomerDto findCustomerById(Integer customerId);

    void updateCustomer(CustomerDto customerDto);

    void delete(Integer customerId);

    Customer findCustomerByName(String name);

    void decrementUsage(Integer customerId);

    Customer findCustomerModelById(Integer id);

    int countTotalCustomers();
}
