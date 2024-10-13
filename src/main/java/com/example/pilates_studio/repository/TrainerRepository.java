package com.example.pilates_studio.repository;

import com.example.pilates_studio.model.Trainer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TrainerRepository extends JpaRepository<Trainer, Integer> {
    Trainer findByName(String trainerName);
}
