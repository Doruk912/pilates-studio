package com.example.pilates_studio.dto;

import com.example.pilates_studio.model.PackageStatus;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
public class PurchaseDto {
    private Integer id;
    private String customerName;
    private Boolean paymentComplete;
    private PackageStatus packageStatus;
    private String description;
    @Min(value = 1, message = "Lesson count must be at least 1")
    private Integer lessonCount;
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
    @Column(precision = 10, scale = 2)
    private BigDecimal amountDue;
}
