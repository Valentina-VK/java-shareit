package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;

import java.util.List;

public interface BookingService {

    BookingDto addBooking(Long userId, NewBookingDto booking);

    BookingDto changeStatus(Long userId, Long bookingId, boolean approved);

    BookingDto getById(Long userId, Long bookingId);

    List<BookingDto> getByBooker(Long userId, BookingSelectionState state);

    List<BookingDto> getByOwner(Long userId, BookingSelectionState state);
}