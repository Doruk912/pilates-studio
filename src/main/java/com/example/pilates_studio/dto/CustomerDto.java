package com.example.pilates_studio.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CustomerDto {
    private Integer Id;
    @NotBlank(message = "Name can not be empty")
    private String name;
    private String phone;
    @Min(value = 0, message = "Remaining usage can not be negative")
    private Integer remainingUsage;
}
