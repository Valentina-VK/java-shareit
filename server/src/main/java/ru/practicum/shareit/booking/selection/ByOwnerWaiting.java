package ru.practicum.shareit.booking.selection;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

public class ByOwnerWaiting extends SelectionHandler {
    private final BookingRepository repository;

    ByOwnerWaiting(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Booking> handle(Long userId, BookingSelectionState state) {
        if (state == BookingSelectionState.WAITING) {
            return repository.findByItemOwnerIdAndStatusOrderByStart(userId, BookingStatus.WAITING);
        } else {
            return handleNext(userId, state);
        }
    }
}