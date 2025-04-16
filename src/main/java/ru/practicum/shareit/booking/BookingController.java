package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDto create(@RequestHeader(USER_ID) Long userId,
                             @Valid @RequestBody NewBookingDto newBooking) {
        return bookingService.addBooking(userId, newBooking);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto changeStatus(@RequestHeader(USER_ID) Long ownerId,
                                   @PathVariable long bookingId,
                                   @RequestParam boolean approved) {
        return bookingService.changeStatus(ownerId, bookingId, approved);
    }

    @GetMapping
    public List<BookingDto> getAllBookings(@RequestHeader(USER_ID) Long userId,
                                           @RequestParam(defaultValue = "ALL") BookingSelectionState state) {
        return bookingService.getByBooker(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllBookingsByOwner(@RequestHeader(USER_ID) Long ownerId,
                                                  @RequestParam(required = false) BookingSelectionState state) {
        return bookingService.getByOwner(ownerId, state != null ? state : BookingSelectionState.ALL);
    }


    @GetMapping("/{bookingId}")
    public BookingDto findById(@RequestHeader(USER_ID) Long userId,
                               @PathVariable long bookingId) {
        return bookingService.getById(userId, bookingId);
    }
}