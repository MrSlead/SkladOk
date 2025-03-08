package com.almod.skladok.utils;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToCompareTypeConverter implements Converter<String, CompareType> {
    @Override
    public CompareType convert(String value) {
        return CompareType.fromValue(value);
    }
}