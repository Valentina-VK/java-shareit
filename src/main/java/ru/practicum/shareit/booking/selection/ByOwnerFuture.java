package ru.practicum.shareit.booking.selection;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.time.Instant;
import java.util.List;

public class ByOwnerFuture extends SelectionHandler {
    private final BookingRepository repository;

    ByOwnerFuture(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Booking> handle(Long userId, BookingSelectionState state) {
        if (state == BookingSelectionState.FUTURE) {
            return repository.findByItemOwnerIdAndStartGreaterThan(userId, Instant.now());
        } else {
            return handleNext(userId, state);
        }
    }
}