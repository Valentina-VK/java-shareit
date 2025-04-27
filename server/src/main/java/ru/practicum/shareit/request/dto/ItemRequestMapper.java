package ru.practicum.shareit.request.dto;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.util.InstantMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = InstantMapper.class)
public interface ItemRequestMapper {

    ItemRequest toEntity(NewItemRequestDto dto);

    ItemRequestDto toDto(ItemRequest request);

    ItemRequestDto toDto(ItemRequest request, List<Item> items);

    List<ItemRequestDto> toDto(List<ItemRequest> requests);
}