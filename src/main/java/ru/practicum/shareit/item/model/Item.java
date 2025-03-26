package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.model.ItemRequest;

@Data
@EqualsAndHashCode(exclude = "available")
public class Item {
    private Long id;
    private Long ownerId;
    private String name;
    private String description;
    private Boolean available;
    private ItemRequest request;
}