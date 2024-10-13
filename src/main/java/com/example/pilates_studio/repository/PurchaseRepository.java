package com.example.pilates_studio.repository;

import com.example.pilates_studio.dto.PurchaseDto;
import com.example.pilates_studio.model.PackageStatus;
import com.example.pilates_studio.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PurchaseRepository extends JpaRepository<Purchase, Integer> {

    @Query("SELECT p FROM Purchase p JOIN p.customer c")
    List<Purchase> findAllPurchasesWithCustomerNames();


    boolean existsByCustomer_IdAndPackageStatus(Integer customerId, PackageStatus packageStatus);

    @Query("SELECT p FROM Purchase p WHERE p.customer.id = :customerId AND p.packageStatus = :packageStatus")
    Purchase findByCustomerIdAndPackageStatus(@Param("customerId") Integer customerId, @Param("packageStatus") PackageStatus packageStatus);

    int countByPackageStatus(PackageStatus packageStatus);
}
