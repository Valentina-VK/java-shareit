package ru.practicum.shareit.item.dto;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValueCheckStrategy;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.util.InstantMapper;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", uses = InstantMapper.class)
public interface ItemMapper {

    Item toEntity(ItemDto dto);

    ItemDto toDto(Item item);

    ItemBookingDatesDto toBookingDatesDto(Item item, Instant lastBooking, Instant nextBooking);

    List<ItemDto> toDto(List<Item> items);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
            nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS)
    void update(ItemDto dto, @MappingTarget Item item);
}