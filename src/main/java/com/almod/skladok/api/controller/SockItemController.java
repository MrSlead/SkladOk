package com.almod.skladok.api.controller;

import com.almod.skladok.store.model.SockItem;
import com.almod.skladok.store.service.SockItemService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/storage/items")
public class SockItemController {
    private final SockItemService sockItemService;

    public SockItemController(@Autowired SockItemService sockItemService) {
        this.sockItemService = sockItemService;
    }

    @PostMapping("/incoming")
    public ResponseEntity<String> addSocks(@Valid @RequestBody SockItem sockItem) {
        sockItemService.addSocks(sockItem);

        return ResponseEntity.ok("Операция выполнена успешна");
    }

    @PostMapping("/outgoing")
    public ResponseEntity<String> removeSocks(@Valid @RequestBody SockItem sockItem) {
        sockItemService.removeSocks(sockItem);

        return ResponseEntity.ok("Операция выполнена успешна");
    }

    @GetMapping
    public ResponseEntity<Integer> getSocksCount(
            @RequestParam @NotBlank String itemColor,
            @RequestParam @NotBlank String compareType,
            @RequestParam @Min(1) @Max(100) Integer materialPercentage) {
        Integer count = sockItemService.getSocksCount(itemColor, compareType, materialPercentage);

        return ResponseEntity.ok(count);
    }
}