package ru.practicum.shareit.booking.selection;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

public class ByOwnerAll extends SelectionHandler {
    private final BookingRepository repository;

    ByOwnerAll(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Booking> handle(Long userId, BookingSelectionState state) {
        if (state == BookingSelectionState.ALL) {
            return repository.findByItemOwnerIdOrderByStart(userId);
        } else {
            return handleNext(userId, state);
        }
    }
}