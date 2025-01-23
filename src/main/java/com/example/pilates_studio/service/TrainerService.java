package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.TrainerDto;
import com.example.pilates_studio.model.Trainer;

import java.util.List;

public interface TrainerService{
    List<TrainerDto> findAllTrainers();

    Trainer saveTrainer(TrainerDto trainerDto);

    TrainerDto findTrainerById(Integer trainerId);

    void updateTrainer(TrainerDto trainerDto);

    void delete(Integer trainerId);

    Trainer findTrainerByName(String trainerName);

    Trainer findTrainer(Integer trainerId);
}
