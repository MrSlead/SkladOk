package com.almod.skladok.store.service;

import com.almod.skladok.api.exception.InsufficientStockException;
import com.almod.skladok.store.model.SockItem;
import com.almod.skladok.store.repository.SockItemRepository;
import com.almod.skladok.utils.CompareType;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SockItemService {
    private final SockItemRepository sockItemRepository;

    public SockItemService(@Autowired SockItemRepository sockItemRepository) {
        this.sockItemRepository = sockItemRepository;
    }

    @Transactional
    public void addSocks(SockItem newSockItem) {
        Optional<SockItem> existingSockItem = sockItemRepository
                .findByItemColorAndMaterialPercentage(newSockItem.getItemColor(), newSockItem.getMaterialPercentage());

        if (existingSockItem.isPresent()) {
            SockItem item = existingSockItem.get();
            item.setUnits(item.getUnits() + newSockItem.getUnits());
            sockItemRepository.save(item);
        } else {
            sockItemRepository.save(newSockItem);
        }
    }

    @Transactional
    public void removeSocks(SockItem sockItem) {
        SockItem existingSockItem = sockItemRepository
                .findByItemColorAndMaterialPercentage(sockItem.getItemColor(), sockItem.getMaterialPercentage())
                .orElseThrow(() -> new EntityNotFoundException("Товар не найден"));

        if(existingSockItem.getUnits() - sockItem.getUnits() < 0) throw new InsufficientStockException("Указанного количества товара нет на складе");
        existingSockItem.setUnits(existingSockItem.getUnits() - sockItem.getUnits());
        sockItemRepository.save(existingSockItem);
    }

    @Transactional(readOnly = true)
    public Integer getSocksCount(String itemColor, String compareType, Integer materialPercentage) {
        List<SockItem> items = switch (CompareType.valueOf(compareType.toUpperCase())) {
            case GT -> sockItemRepository.findByItemColorAndMaterialPercentageGreaterThan(itemColor, materialPercentage);
            case LT -> sockItemRepository.findByItemColorAndMaterialPercentageLessThan(itemColor, materialPercentage);
            case EQ -> sockItemRepository.findByItemColorAndMaterialPercentageEquals(itemColor, materialPercentage);
        };
        return items.stream()
                .mapToInt(SockItem::getUnits)
                .sum();
    }
}
