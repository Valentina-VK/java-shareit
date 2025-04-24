package ru.practicum.shareit.booking.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.InstantMapper;

import java.util.List;

@Mapper(componentModel = "spring", uses = InstantMapper.class)
public interface BookingMapper {

    @Mapping(ignore = true, target = "id")
    @Mapping(source = "dto.start", target = "start")
    @Mapping(source = "dto.end", target = "end")
    Booking toEntity(NewBookingDto dto, User booker, Item item);

    BookingDto toDto(Booking booking);

    List<BookingDto> toDto(List<Booking> bookings);
}