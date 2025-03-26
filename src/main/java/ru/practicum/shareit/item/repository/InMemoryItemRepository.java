package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Repository
public class InMemoryItemRepository implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private long lastId = 0;

    public List<Item> getAllItems(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(userId))
                .toList();
    }

    public List<Item> searchByText(String text) {
        return items.values().stream()
                .filter(item -> (item.getName().toLowerCase().contains(text)
                                 || item.getDescription().toLowerCase().contains(text))
                                && item.getAvailable())
                .toList();
    }

    public Item getItem(Long itemId) {
        return Optional.ofNullable(items.get(itemId))
                .orElseThrow(() -> new NotFoundException("Вещь не найдена, id: " + itemId));
    }

    public Item saveItem(Item item) {
        item.setId(getNextId());
        items.put(item.getId(), item);
        return item;
    }

    public Item updateItem(Item item) {
        items.put(item.getId(), item);
        return item;
    }

    public void deleteItem(Long itemId) {
        items.remove(itemId);
    }

    private long getNextId() {
        return ++lastId;
    }
}