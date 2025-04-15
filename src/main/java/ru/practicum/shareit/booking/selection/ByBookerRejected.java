package ru.practicum.shareit.booking.selection;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

public class ByBookerRejected extends SelectionHandler {
    private final BookingRepository repository;

    ByBookerRejected(BookingRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Booking> handle(Long userId, BookingSelectionState state) {
        if (state == BookingSelectionState.REJECTED) {
            return repository.findByBookerIdAndStatusOrderByStart(userId, BookingStatus.REJECTED);
        } else {
            return handleNext(userId, state);
        }
    }
}