package com.almod.skladok.api.mapper;

import com.almod.skladok.api.dto.SockItemDto;
import com.almod.skladok.store.model.SockItem;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface SockItemMapper {
    SockItemMapper MAPPER = Mappers.getMapper(SockItemMapper.class);

    SockItemDto mapToSockItemDto(SockItem sockItem);
    SockItem mapToSockItem(SockItemDto sockItemDto);
}
