package com.almod.skladok.api.controller;

import com.almod.skladok.api.dto.SockItemDto;
import com.almod.skladok.api.dto.SocksCountResponse;
import com.almod.skladok.api.mapper.SockItemMapper;
import com.almod.skladok.store.service.SockItemService;
import com.almod.skladok.utils.CompareType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOG = LoggerFactory.getLogger(SockItemController.class);

    private final SockItemService sockItemService;

    public SockItemController(@Autowired SockItemService sockItemService) {
        this.sockItemService = sockItemService;
    }

    @PostMapping("/incoming")
    public ResponseEntity<String> addSocks(@Valid @RequestBody SockItemDto sockItemDto) {
        LOG.info("Поступил запрос на загрузку носочных изделий: Color: {}, Material Percentage: {},  Units: {}", sockItemDto.getItemColor(), sockItemDto.getMaterialPercentage(), sockItemDto.getUnits());
        sockItemService.addSocks(SockItemMapper.MAPPER.mapToSockItem(sockItemDto));

        return ResponseEntity.ok("Операция выполнена успешна");
    }

    @PostMapping("/outgoing")
    public ResponseEntity<String> removeSocks(@Valid @RequestBody SockItemDto sockItemDto) {
        LOG.info("Поступил запрос на отгрузку носочных изделий: Color: {}, Material Percentage: {},  Units: {}", sockItemDto.getItemColor(), sockItemDto.getMaterialPercentage(), sockItemDto.getUnits());
        sockItemService.removeSocks(SockItemMapper.MAPPER.mapToSockItem(sockItemDto));

        return ResponseEntity.ok("Операция выполнена успешна");
    }

    @GetMapping
    public ResponseEntity<SocksCountResponse> getSocksCount(
            @RequestParam @NotBlank String itemColor,
            @RequestParam CompareType compareType,
            @RequestParam @Min(1) @Max(100) Integer materialPercentage) {
        LOG.info("Поступил запрос на получение информации о носочных изделиях: Color: {}, Material Percentage: {}", itemColor, materialPercentage);
        Integer count = sockItemService.getSocksCount(itemColor, compareType, materialPercentage);

        SocksCountResponse response = new SocksCountResponse(count);

        return ResponseEntity.ok(response);
    }
}