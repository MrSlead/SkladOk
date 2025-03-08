package com.almod.skladok.api.exception;

import com.almod.skladok.api.controller.SockItemController;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger LOG = LoggerFactory.getLogger(SockItemController.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationErrors(MethodArgumentNotValidException ex) {
        Stream<String> fieldErrors = ex.getFieldErrors().stream()
                .map(e -> String.format("%s: %s", e.getField(), e.getDefaultMessage()));

        Stream<String> globalErrors = ex.getGlobalErrors().stream()
                .map(e -> String.format("%s: %s\n", e.getObjectName(), e.getDefaultMessage()));

        String error = Stream.concat(fieldErrors, globalErrors)
                .collect(Collectors.joining("\n"));

        LOG.warn("Ошибочный запрос от клиента: {}", error);
        return new ResponseEntity<>(error, new HttpHeaders(), HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        if (ex.getRequiredType() != null && ex.getRequiredType().isEnum()) {
            return new ResponseEntity<>("Невалидное значение для enum. Разрешенные значения: " +
                    Arrays.toString(ex.getRequiredType().getEnumConstants()), HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Невалидный параметр: " + ex.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({EntityNotFoundException.class, InsufficientStockException.class})
    public ResponseEntity<String> handleEntityNotFoundException(Exception e) {
        LOG.warn("Ошибочный запрос от клиента: {}", e.getMessage());
        return new ResponseEntity<>("Ошибка запроса: " + e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        return new ResponseEntity<>("Ошибка на сервере: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}