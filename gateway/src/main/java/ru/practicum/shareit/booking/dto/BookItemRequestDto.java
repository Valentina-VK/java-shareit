package ru.practicum.shareit.booking.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
    @NotNull
    @Positive
    private long itemId;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    @Future
    private LocalDateTime end;
}