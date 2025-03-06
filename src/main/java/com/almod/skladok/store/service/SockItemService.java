package com.almod.skladok.store.service;

import com.almod.skladok.store.model.SockItem;
import com.almod.skladok.store.repository.SockItemRepository;
import com.almod.skladok.utils.CompareType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SockItemService {
    private final SockItemRepository sockItemRepository;

    public SockItemService(@Autowired SockItemRepository sockItemRepository) {
        this.sockItemRepository = sockItemRepository;
    }

    public void addSocks(SockItem newSockItem) {
        Optional<SockItem> existingSockItem =
                sockItemRepository.findByItemColorAndMaterialPercentage(newSockItem.getItemColor(), newSockItem.getMaterialPercentage());

        if (existingSockItem.isPresent()) {
            SockItem item = existingSockItem.get();
            item.setUnits(item.getUnits() + newSockItem.getUnits());
            sockItemRepository.save(item);
        } else {
            sockItemRepository.save(newSockItem);
        }
    }

    public void removeSocks(SockItem sockItem) {
        SockItem existingSockItem = sockItemRepository.findByItemColorAndMaterialPercentage(sockItem.getItemColor(), sockItem.getMaterialPercentage())
                .orElseThrow(() -> new RuntimeException("Товар не найден"));
        // Рассмотреть Если на удаление просят больше
        existingSockItem.setUnits(existingSockItem.getUnits() - sockItem.getUnits());
        sockItemRepository.save(existingSockItem);
    }

    public Integer getSocksCount(String itemColor, CompareType compareType, Integer materialPercentage) {
        List<SockItem> items = switch (compareType) {
            case GT -> sockItemRepository.findByItemColorAndMaterialPercentageGreaterThan(itemColor, materialPercentage);
            case LT -> sockItemRepository.findByItemColorAndMaterialPercentageLessThan(itemColor, materialPercentage);
            case EQ -> sockItemRepository.findByItemColorAndMaterialPercentageEquals(itemColor, materialPercentage);
            default -> throw new IllegalArgumentException("Неподдерживаемый тип сравнения");
        };
        return items.stream()
                .mapToInt(SockItem::getUnits)
                .sum();
    }
}
