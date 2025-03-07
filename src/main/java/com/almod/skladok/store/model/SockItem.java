package com.almod.skladok.store.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@Entity
@Table
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
@IdClass(SockItemPK.class)
public class SockItem implements Serializable {
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
