package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class NewBookingDto {
    @NotNull
    private String start;
    @NotNull
    private String end;
    @NotNull
    @Positive
    private Long itemId;
}