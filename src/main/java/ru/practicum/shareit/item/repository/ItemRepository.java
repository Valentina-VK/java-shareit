package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository {

    List<Item> getAllItems(Long userId);

    List<Item> searchByText(String text);

    Item getItem(Long itemId);

    Item saveItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Long itemId);
}
