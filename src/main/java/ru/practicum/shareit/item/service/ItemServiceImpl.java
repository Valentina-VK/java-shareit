package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper mapper;

    @Override
    public List<ItemDto> getAll(Long userId) {
        userRepository.get(userId);
        return mapper.toDto(itemRepository.getAllItems(userId));
    }

    @Override
    public ItemDto get(Long userId, Long itemId) {
        userRepository.get(userId);
        return mapper.toDto(itemRepository.getItem(itemId));
    }

    @Override
    public List<ItemDto> search(Long userId, String text) {
        userRepository.get(userId);
        if (text == null || text.isBlank()) return List.of();
        return mapper.toDto(itemRepository.searchByText(text.toLowerCase()));
    }

    @Override
    public ItemDto save(Long userId, ItemDto item) {
        userRepository.get(userId);
        Item newItem = mapper.toEntity(item);
        if (newItem != null)
            newItem.setOwnerId(userId);
        return mapper.toDto(itemRepository.saveItem(newItem));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        userRepository.get(userId);
        Item oldItem = itemRepository.getItem(itemId);
        if (oldItem.getOwnerId().longValue() != userId.longValue())
            throw new NoAccessException("Обновление данных доступно только владельцу вещи");
        mapper.update(item, oldItem);
        return mapper.toDto(itemRepository.updateItem(oldItem));
    }

    @Override
    public void delete(Long userId, Long itemId) {
        userRepository.get(userId);
        if (itemRepository.getItem(itemId).getOwnerId().longValue() != userId.longValue())
            throw new NoAccessException("Удаление вещи доступно только владельцу");
        itemRepository.deleteItem(itemId);
    }
}