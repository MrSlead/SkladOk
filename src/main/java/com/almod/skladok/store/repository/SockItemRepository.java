package com.almod.skladok.store.repository;

import com.almod.skladok.store.model.SockItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SockItemRepository extends JpaRepository<SockItem, Long> {
    Optional<SockItem> findByItemColorAndMaterialPercentage(String itemColor, Integer materialPercentage);
    List<SockItem> findByItemColorAndMaterialPercentageGreaterThan(String itemColor, Integer materialPercentage);
    List<SockItem> findByItemColorAndMaterialPercentageLessThan(String itemColor, Integer materialPercentage);
    List<SockItem> findByItemColorAndMaterialPercentageEquals(String itemColor, Integer materialPercentage);
}