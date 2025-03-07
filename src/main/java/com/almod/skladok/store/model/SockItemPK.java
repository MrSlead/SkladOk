package com.almod.skladok.store.model;

import lombok.Data;

import java.io.Serializable;

@Data
public class SockItemPK implements Serializable {
    private String itemColor;
    private Integer materialPercentage;
}
