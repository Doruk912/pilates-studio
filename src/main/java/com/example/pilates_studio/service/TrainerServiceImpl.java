package com.example.pilates_studio.service;

import com.example.pilates_studio.dto.TrainerDto;
import com.example.pilates_studio.model.Customer;
import com.example.pilates_studio.model.Trainer;
import com.example.pilates_studio.repository.CustomerRepository;
import com.example.pilates_studio.repository.TrainerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TrainerServiceImpl implements TrainerService{
    private TrainerRepository trainerRepository;

    @Autowired
    public TrainerServiceImpl(TrainerRepository trainerRepository){
        this.trainerRepository = trainerRepository;
    }

    @Override
    public List<TrainerDto> findAllTrainers() {
        List<Trainer> trainers = trainerRepository.findAll();
        return trainers.stream().sorted(Comparator.comparing(Trainer::getName)).map((trainer) -> mapToTrainerDto(trainer)).collect(Collectors.toList());
    }

    @Override
    public Trainer saveTrainer(TrainerDto trainerDto) {
        Trainer trainer = mapToTrainer(trainerDto);
        return trainerRepository.save(trainer);
    }

    @Override
    public TrainerDto findTrainerById(Integer trainerId) {
        Trainer trainer = trainerRepository.findById(trainerId).get();
        return mapToTrainerDto(trainer);
    }

    @Override
    public void updateTrainer(TrainerDto trainerDto) {
        Trainer trainer = mapToTrainer(trainerDto);
        trainerRepository.save(trainer);
    }

    @Override
    public void delete(Integer trainerId) {
        trainerRepository.deleteById(trainerId);
    }

    @Override
    public Trainer findTrainerByName(String trainerName) {
        return trainerRepository.findByName(trainerName);
    }

    private Trainer mapToTrainer(TrainerDto trainerDto){
        return Trainer.builder()
                .id(trainerDto.getId())
                .name(trainerDto.getName())
                .build();
    }
    private TrainerDto mapToTrainerDto(Trainer trainer){
        return TrainerDto.builder()
                .id(trainer.getId())
                .name(trainer.getName())
                .build();
    }
}
