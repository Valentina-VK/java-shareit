package ru.practicum.shareit.booking.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingSelectionState;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.InstantMapper;
import ru.practicum.shareit.validateService.ValidateService;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ValidateService validateService;
    private final BookingMapper mapper;

    @Override
    public BookingDto addBooking(Long userId, NewBookingDto booking) {
        checkDatesBooking(booking.getStart(), booking.getEnd());
        User booker = validateService.checkUser(userId);
        Item item = validateService.checkItem(booking.getItemId());
        if (item.getOwnerId().equals(userId)) throw new NoAccessException("Владелец вещи не может ее бронировать");
        if (!item.getAvailable()) throw new ValidationException("Вещь не доступна для броннирования");
        Booking newBooking = mapper.toEntity(booking, booker, item);
        if (newBooking == null) throw new RuntimeException("Не удалось сохранить запрос на бронирование");
        return mapper.toDto(bookingRepository.save(newBooking));
    }

    @Override
    public BookingDto changeStatus(Long userId, Long bookingId, boolean approved) {
        Booking booking = validateService.checkBooking(bookingId);
        if (!booking.getItem().getOwnerId().equals(userId))
            throw new NoAccessException("Только владелец вещи может изменить статус бронирования");
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
        if (!booking.getItem().getOwnerId().equals(userId) && !booking.getBooker().getId().equals(userId))
            throw new NoAccessException("Информация доступна только владельцу вещи или автором бронирования");
        return mapper.toDto(booking);
    }

    @Override
    public List<BookingDto> getByBooker(Long userId, BookingSelectionState state) {
        validateService.checkUser(userId);
        switch (state) {
            case ALL -> {
                return mapper.toDto(bookingRepository.findByBookerIdOrderByStart(userId));
            }
            case WAITING -> {
                return mapper.toDto(bookingRepository.findByBookerIdAndStatusOrderByStart(userId,
                        BookingStatus.WAITING));
            }
            case REJECTED -> {
                return mapper.toDto(bookingRepository.findByBookerIdAndStatusOrderByStart(userId,
                        BookingStatus.REJECTED));
            }
            case CURRENT -> {
                return mapper.toDto(bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThan(userId,
                        Instant.now(), Instant.now()));
            }
            case PAST -> {
                return mapper.toDto(bookingRepository.findByBookerIdAndEndLessThan(userId, Instant.now()));
            }
            case FUTURE -> {
                return mapper.toDto(bookingRepository.findByBookerIdAndStartGreaterThan(userId, Instant.now()));
            }
        }
        return List.of();
    }

    @Override
    public List<BookingDto> getByOwner(Long userId, BookingSelectionState state) {
        validateService.checkUser(userId);
        switch (state) {
            case ALL -> {
                return mapper.toDto(bookingRepository.findByItemOwnerIdOrderByStart(userId));
            }
            case WAITING -> {
                return mapper.toDto(bookingRepository.findByItemOwnerIdAndStatusOrderByStart(userId,
                        BookingStatus.WAITING));
            }
            case REJECTED -> {
                return mapper.toDto(bookingRepository.findByItemOwnerIdAndStatusOrderByStart(userId,
                        BookingStatus.REJECTED));
            }
            case CURRENT -> {
                return mapper.toDto(bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThan(userId,
                        Instant.now(), Instant.now()));
            }
            case PAST -> {
                return mapper.toDto(bookingRepository.findByItemOwnerIdAndEndLessThan(userId, Instant.now()));
            }
            case FUTURE -> {
                return mapper.toDto(bookingRepository.findByItemOwnerIdAndStartGreaterThan(userId, Instant.now()));
            }
        }
        return List.of();
    }

    private void checkDatesBooking(String start, String end) {
        Instant startDate = InstantMapper.mapStringToInstant(start);
        Instant endDate = InstantMapper.mapStringToInstant(end);
        if (startDate.isBefore(Instant.now()) || endDate.isBefore(Instant.now())
            || startDate.isAfter(endDate) || startDate.equals(endDate))
            throw new ValidationException("Некорректные даты броннирования");
    }
}