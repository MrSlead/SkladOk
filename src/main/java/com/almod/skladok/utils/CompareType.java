package com.almod.skladok.utils;

import lombok.Getter;

@Getter
public enum CompareType {
    GT("gt"),
    LT("lt"),
    EQ("eq");

    private final String value;

    CompareType(String value) {
        this.value = value;
    }
}