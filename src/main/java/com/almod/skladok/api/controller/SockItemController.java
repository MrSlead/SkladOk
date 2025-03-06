package com.almod.skladok.api.controller;

import com.almod.skladok.store.model.SockItem;
import com.almod.skladok.store.service.SockItemService;
import com.almod.skladok.utils.CompareType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/storage/items")
public class SockItemController {
    private final SockItemService sockItemService;

    public SockItemController(@Autowired SockItemService sockItemService) {
        this.sockItemService = sockItemService;
    }

    @PostMapping("/incoming")
    public ResponseEntity<String> addSocks(@RequestBody SockItem sockItem) {
        sockItemService.addSocks(sockItem);

        return ResponseEntity.ok("Товар успешно добавлен");
    }

    @PostMapping("/outgoing")
    public ResponseEntity<String> removeSocks(@RequestBody SockItem sockItem) {
        sockItemService.removeSocks(sockItem);

        return ResponseEntity.ok("Товар успешно списан");
    }

    @GetMapping
    public ResponseEntity<Integer> getSocksCount(
            @RequestParam String itemColor,
            @RequestParam CompareType compareType,
            @RequestParam Integer materialPercentage) {
        Integer count = sockItemService.getSocksCount(itemColor, compareType, materialPercentage);

        return ResponseEntity.ok(count);
    }
}