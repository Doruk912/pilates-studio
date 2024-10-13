package com.example.pilates_studio.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
public class Purchase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    private boolean paymentComplete;
    @Enumerated(EnumType.STRING)
    private PackageStatus packageStatus;
    private String description;
    private Integer lessonCount;

    public Integer getCustomerId(){
        return this.customer.getId();
    }
}
