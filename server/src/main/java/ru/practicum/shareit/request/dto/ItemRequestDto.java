package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import java.time.Instant;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private User requestor;
    private Instant created;
    private List<ItemByRequestDto> items;
}