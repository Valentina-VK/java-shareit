package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    List<ItemDto> getAll(Long userId);

    ItemDto get(Long userId, Long itemId);

    List<ItemDto> search(Long userId, String text);

    ItemDto save(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    void delete(Long userId, Long itemId);
}