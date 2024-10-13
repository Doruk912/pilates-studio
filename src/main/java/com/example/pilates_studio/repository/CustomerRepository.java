package com.example.pilates_studio.repository;

import com.example.pilates_studio.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    Customer findByName(String name);
}
