package ru.practicum.shareit.item.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ItemMapper {

    Item toEntity(ItemDto dto);

    ItemRequest toEntity(ItemRequestDto request);

    ItemRequestDto toDto(ItemRequest request);

    ItemDto toDto(Item item);

    List<ItemDto> toDto(List<Item> items);
}