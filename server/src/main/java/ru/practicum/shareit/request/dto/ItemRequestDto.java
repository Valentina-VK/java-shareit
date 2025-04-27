package ru.practicum.shareit.request.dto;

import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.Instant;
import java.util.List;

@Data
public class ItemRequestDto {
    private Long id;
    private String description;
    private UserDto requestor;
    private Instant created;
    private List<ItemByRequestDto> items;
}