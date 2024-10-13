package com.example.pilates_studio.controller;

import com.example.pilates_studio.dto.TrainerDto;
import com.example.pilates_studio.model.Trainer;
import com.example.pilates_studio.service.TrainerService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class TrainerController {
    private TrainerService trainerService;

    @Autowired
    public TrainerController(TrainerService trainerService) {this.trainerService = trainerService;}

    @GetMapping("/trainers")
    public String listTrainers(Model model){
        List<TrainerDto> trainers = trainerService.findAllTrainers();
        model.addAttribute("trainers", trainers);
        return "trainers-list";
    }

    @GetMapping("/trainers/new")
    public String createTrainerForm(Model model){
        Trainer trainer = new Trainer();
        model.addAttribute("trainer", trainer);
        return "trainers-create";
    }

    @PostMapping("/trainers/new")
    public String saveTrainer(@Valid @ModelAttribute("trainer") TrainerDto trainerDto, BindingResult result, Model model){
        if(result.hasErrors()){
            model.addAttribute("trainer", trainerDto);
            return "trainers-create";
        }
        trainerService.saveTrainer(trainerDto);
        return "redirect:/trainers";
    }

    @GetMapping("/trainers/{trainerId}/edit")
    public String editTrainerForm(@PathVariable("trainerId") Integer trainerId, Model model){
        TrainerDto trainer = trainerService.findTrainerById(trainerId);
        model.addAttribute("trainer", trainer);
        return "trainers-edit";
    }

    @PostMapping("/trainers/{trainerId}/edit")
    public String updateTrainer(@PathVariable("trainerId") Integer trainerId, @Valid @ModelAttribute("trainer") TrainerDto trainerDto, BindingResult result){
        if(result.hasErrors()){
            return "trainers-edit";
        }
        trainerDto.setId(trainerId);
        trainerService.updateTrainer(trainerDto);
        return "redirect:/trainers";
    }

    @GetMapping("/trainers/{trainerId}/delete")
    public String deleteTrainer(@PathVariable("trainerId") Integer trainerId, RedirectAttributes redirectAttributes){
        try {
            trainerService.delete(trainerId);
            redirectAttributes.addFlashAttribute("successMessage", "Trainer deleted successfully");
        }catch (Exception e){
            redirectAttributes.addFlashAttribute("errorMessage", "Unable to delete trainer. This may be due to the trainer having an appointment");
        }
        return "redirect:/trainers";
    }
}
