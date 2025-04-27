package ru.practicum.shareit.booking.selection;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SelectionService {
    private final BookingRepository repository;
    private SelectionHandler handler;

    public List<Booking> handleByBooker(Long userId, BookingSelectionState state) {
        handler = SelectionHandler.link(
                new ByBookerAll(repository),
                new ByBookerWaiting(repository),
                new ByBookerRejected(repository),
                new ByBookerCurrent(repository),
                new ByBookerPast(repository),
                new ByBookerFuture(repository));
        return handler.handle(userId, state);
    }

    public List<Booking> handleByOwner(Long userId, BookingSelectionState state) {
        handler = SelectionHandler.link(
                new ByOwnerAll(repository),
                new ByOwnerWaiting(repository),
                new ByOwnerRejected(repository),
                new ByOwnerCurrent(repository),
                new ByOwnerPast(repository),
                new ByOwnerFuture(repository));
        return handler.handle(userId, state);
    }
}