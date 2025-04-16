package ru.practicum.shareit.booking.selection;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.Instant;
import java.util.List;

public class ByBookerPast extends SelectionHandler {
    private final BookingRepository repository;

    ByBookerPast(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Booking> handle(Long userId, BookingSelectionState state) {
        if (state == BookingSelectionState.PAST) {
            return repository.findByBookerIdAndEndLessThan(userId, Instant.now());
        } else {
            return handleNext(userId, state);
        }
    }
}