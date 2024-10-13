package com.example.pilates_studio.dto;

import com.example.pilates_studio.model.AppointmentStatus;
import lombok.Builder;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
@Builder
public class AppointmentDto {
    private Integer id;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;
    private String customerName;
    private String trainerName;
    private String locationId;
    private AppointmentStatus appointmentStatus;
    private String description;
}
