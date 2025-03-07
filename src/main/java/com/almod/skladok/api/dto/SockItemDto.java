package com.almod.skladok.api.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class SockItemDto {
    @Id
    @NotBlank
    private String itemColor;

    @Id
    @Min(1)
    @Max(100)
    @NotNull
    private Integer materialPercentage;

    @Min(1)
    @NotNull
    private Integer units;
}
