package com.example.pilates_studio.repository;

import com.example.pilates_studio.model.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, String> {
    List<Location> findByFranchise(String franchise);
}
