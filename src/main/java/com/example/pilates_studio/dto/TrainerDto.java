package com.example.pilates_studio.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TrainerDto {
    private Integer id;
    @NotBlank(message = "Name can not be empty")
    private String name;
}
