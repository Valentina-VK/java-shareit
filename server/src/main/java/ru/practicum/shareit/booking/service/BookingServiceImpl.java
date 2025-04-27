package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.selection.SelectionService;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validateService.ValidateService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ValidateService validateService;
    private final BookingMapper mapper;
    private final SelectionService selectionService;

    @Override
    public BookingDto addBooking(Long userId, NewBookingDto booking) {
        User booker = validateService.checkUser(userId);
        Item item = validateService.checkItem(booking.getItemId());
        if (item.getOwnerId().equals(userId)) {
            throw new NoAccessException("Владелец вещи не может ее бронировать");
        }
        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь не доступна для броннирования");
        }
        Booking newBooking = mapper.toEntity(booking, booker, item);
        if (newBooking == null) {
            throw new RuntimeException("Не удалось сохранить запрос на бронирование");
        }
        return mapper.toDto(bookingRepository.save(newBooking));
    }

    @Override
    public BookingDto changeStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = validateService.checkBooking(bookingId);
        if (!isOwnerId(booking, userId)) {
            throw new NoAccessException("Только владелец вещи может изменить статус бронирования");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return mapper.toDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getById(Long userId, Long bookingId) {
        Booking booking = validateService.checkBooking(bookingId);
        if (!isOwnerId(booking, userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NoAccessException("Информация доступна только владельцу вещи или автором бронирования");
        }
        return mapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, BookingSelectionState state) {
        validateService.checkUser(userId);
        return mapper.toDto(selectionService.handleByBooker(userId, state));
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, BookingSelectionState state) {
        validateService.checkUser(userId);
        return mapper.toDto(selectionService.handleByOwner(userId, state));
    }

    private boolean isOwnerId(Booking booking, Long userId) {
        return booking.getItem().getOwnerId().equals(userId);
    }
}