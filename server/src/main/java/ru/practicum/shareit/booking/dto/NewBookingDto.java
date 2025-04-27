package ru.practicum.shareit.booking.dto;

import lombok.Data;

@Data
public class NewBookingDto {

    private String start;
    private String end;
    private Long itemId;
}