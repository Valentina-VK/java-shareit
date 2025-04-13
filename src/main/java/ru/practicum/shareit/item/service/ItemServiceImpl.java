package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.item.dto.ItemBookingDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.validateService.ValidateService;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ValidateService validateService;
    private final BookingRepository bookingRepository;
    private final ItemMapper mapper;

    @Override
    public List<ItemDto> getAll(Long userId) {
        validateService.checkUser(userId);
        return mapper.toDto(itemRepository.findByOwnerId(userId));
    }

    @Override
    public ItemBookingDatesDto get(Long userId, Long itemId) {
        validateService.checkUser(userId);
        Item item = validateService.checkItem(itemId);
        Instant lastBooking = null;
        Instant nextBooking = null;
        if (item.getOwnerId().equals(userId)) {
            List<Booking> bookings = bookingRepository.findByItemIdOrderByStart(itemId);
            if (bookings != null && !bookings.isEmpty()) {
                lastBooking = bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.CANCELED))
                        .map(Booking::getEnd)
                        .max(Instant::compareTo).orElse(null);
                nextBooking = bookings.stream()
                        .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                        .map(Booking::getStart)
                        .min(Instant::compareTo).orElse(null);
            }
        }
        return mapper.toBookingDatesDto(item, lastBooking, nextBooking);
    }


    @Override
    public List<ItemDto> search(Long userId, String text) {
        validateService.checkUser(userId);
        if (text == null || text.isBlank()) return List.of();
        return mapper.toDto(itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue(text));
    }

    @Override
    public ItemDto save(Long userId, ItemDto item) {
        validateService.checkUser(userId);
        Item newItem = mapper.toEntity(item);
        if (newItem == null) throw new RuntimeException("Не удалось сохранить вещь");
        newItem.setOwnerId(userId);
        return mapper.toDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        validateService.checkUser(userId);
        Item oldItem = validateService.checkItem(itemId);
        if (oldItem.getOwnerId().longValue() != userId.longValue())
            throw new NoAccessException("Обновление данных доступно только владельцу вещи");
        mapper.update(item, oldItem);
        return mapper.toDto(itemRepository.save(oldItem));
    }

    @Override
    public void delete(Long userId, Long itemId) {
        validateService.checkUser(userId);
        Item item = validateService.checkItem(itemId);
        if (item.getOwnerId().longValue() != userId.longValue())
            throw new NoAccessException("Удаление вещи доступно только владельцу");
        itemRepository.deleteById(itemId);
    }
}