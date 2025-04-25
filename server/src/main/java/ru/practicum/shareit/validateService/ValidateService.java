package ru.practicum.shareit.validateService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;

@Service
@RequiredArgsConstructor
public class ValidateService {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final ItemRequestRepository requestRepository;

    public User checkUser(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден, id: " + id));
    }

    public Item checkItem(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена, id: " + id));
    }

    public Booking checkBooking(Long id) {
        return bookingRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Броннирование не найдено, id: " + id));
    }

    public boolean hasPastBooking(Long userId, Long itemId) {
        System.out.println("user" +userId);
        return !bookingRepository.findByBookerIdAndItemIdAndStatusAndEndLessThan(userId, itemId,
                BookingStatus.APPROVED, Instant.now()).isEmpty();
    }

    public ItemRequest checkRequest(Long requestId) {
        return requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Запрос вещи не найден, id: " + requestId));
    }
}