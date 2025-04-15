package ru.practicum.shareit.booking.selection;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public abstract class SelectionHandler {
    private SelectionHandler next;

    public static SelectionHandler link(SelectionHandler first, SelectionHandler... chain) {
        SelectionHandler head = first;
        for (SelectionHandler nextInChain : chain) {
            head.next = nextInChain;
            head = nextInChain;
        }
        return first;
    }

    public abstract List<Booking> handle(Long userId, BookingSelectionState state);

    protected List<Booking> handleNext(Long userId, BookingSelectionState state) {
        if (next == null) {
            return List.of();
        }
        return next.handle(userId, state);
    }
}