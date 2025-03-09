package com.almod.skladok.store.service;

import com.almod.skladok.api.controller.SockItemController;
import com.almod.skladok.api.exception.InsufficientStockException;
import com.almod.skladok.store.model.SockItem;
import com.almod.skladok.store.repository.SockItemRepository;
import com.almod.skladok.utils.CompareType;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SockItemService {
    private static final Logger LOG = LoggerFactory.getLogger(SockItemService.class);

    private final SockItemRepository sockItemRepository;

    public SockItemService(@Autowired SockItemRepository sockItemRepository) {
        this.sockItemRepository = sockItemRepository;
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void addSocks(SockItem newSockItem) {
        Optional<SockItem> existingSockItem = sockItemRepository
                .findByItemColorAndMaterialPercentage(newSockItem.getItemColor(), newSockItem.getMaterialPercentage());

        if (existingSockItem.isPresent()) {
            SockItem item = existingSockItem.get();
            int newUnits = newSockItem.getUnits();
            item.setUnits(item.getUnits() + newSockItem.getUnits());
            sockItemRepository.save(item);

            LOG.info("Принята загрузка уже имеющих носочных изделий: Color - {}, Material Percentage - {},  Units - {}", item.getItemColor(), item.getMaterialPercentage(), newUnits);
        } else {
            sockItemRepository.save(newSockItem);

            LOG.info("Принята новая загрузка носочных изделий: Color - {}, Material Percentage - {},  Units - {}", newSockItem.getItemColor(), newSockItem.getMaterialPercentage(), newSockItem.getUnits());
        }
    }

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public void removeSocks(SockItem sockItem) {
        SockItem existingSockItem = sockItemRepository
                .findByItemColorAndMaterialPercentage(sockItem.getItemColor(), sockItem.getMaterialPercentage())
                .orElseThrow(() -> new EntityNotFoundException(String.format("Товар не найден: Color - %s, Material Percentage - %s, Units - %s", sockItem.getItemColor(), sockItem.getMaterialPercentage(), sockItem.getUnits())));

        if(existingSockItem.getUnits() - sockItem.getUnits() < 0) throw new InsufficientStockException(String.format("Указанного количества товара нет на складе: Color - %s, Material Percentage - %s, Units - %s", sockItem.getItemColor(), sockItem.getMaterialPercentage(), sockItem.getUnits()));
        existingSockItem.setUnits(existingSockItem.getUnits() - sockItem.getUnits());
        sockItemRepository.save(existingSockItem);

        LOG.info("Принята отгрузка носочных изделий: Color - {}, Material Percentage - {},  Units - {}", sockItem.getItemColor(), sockItem.getMaterialPercentage(), sockItem.getUnits());
    }

    @Transactional(readOnly = true)
    public Integer getSocksCount(String itemColor, CompareType compareType, Integer materialPercentage) {
        List<SockItem> items = switch (compareType) {
            case GT -> sockItemRepository.findByItemColorAndMaterialPercentageGreaterThan(itemColor, materialPercentage);
            case LT -> sockItemRepository.findByItemColorAndMaterialPercentageLessThan(itemColor, materialPercentage);
            case EQ -> sockItemRepository.findByItemColorAndMaterialPercentageEquals(itemColor, materialPercentage);
        };
        return items.stream()
                .mapToInt(SockItem::getUnits)
                .sum();
    }
}
