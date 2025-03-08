package com.almod.skladok.utils;

import lombok.Getter;
import java.util.Arrays;

@Getter
public enum CompareType {
    GT("gt"),
    LT("lt"),
    EQ("eq");

    private final String value;

    CompareType(String value) {
        this.value = value;
    }

    public static CompareType fromValue(String value) {
        return Arrays.stream(CompareType.values())
                .filter(compareType -> compareType.value.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Невалидный compareType: " + value));
    }
}