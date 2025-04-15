package ru.practicum.shareit.booking.selection;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

public class ByOwnerRejected extends SelectionHandler {
    private final BookingRepository repository;

    ByOwnerRejected(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Booking> handle(Long userId, BookingSelectionState state) {
        if (state == BookingSelectionState.REJECTED) {
            return repository.findByItemOwnerIdAndStatusOrderByStart(userId, BookingStatus.REJECTED);
        } else {
            return handleNext(userId, state);
        }
    }
}