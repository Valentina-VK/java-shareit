package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDto createItemRequest(Long userId, NewItemRequestDto newDto);

    List<ItemRequestDto> getAllByUser(Long userId);

    List<ItemRequestDto> getAll();

    ItemRequestDto getById(Long requestId);
}