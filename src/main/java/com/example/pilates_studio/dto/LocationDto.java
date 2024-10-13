package com.example.pilates_studio.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LocationDto {
    private String id;
    @NotBlank(message = "Franchise can not be empty")
    @Size(min = 2, max = 2, message = "Franchise must be exactly two letters")
    @Pattern(regexp = "[a-zA-Z]+", message = "Franchise must contain only letters")
    private String franchise;
    @NotBlank(message = "Room can not be empty")
    @Size(min = 4, max = 4, message = "Room must be exactly four letters")
    private String room;
    @NotBlank(message = "Slot can not be empty")
    @Size(max = 15, message = "Slot can be at most 15 characters long")
    private String slot;
}
