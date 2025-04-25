package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;
import static ru.practicum.shareit.TestConstant.TIME_AFTER;
import static ru.practicum.shareit.TestConstant.TIME_BEFORE;

class ItemMapperTest {

    private final ItemMapper mapper = Mappers.getMapper(ItemMapper.class);

    @Test
    void toEntity_shouldMapFromDto() {
        ItemDto dto = new ItemDto();
        dto.setId(55L);
        dto.setName("Test item");
        dto.setDescription("for test");
        dto.setAvailable(true);

        Item result = mapper.toEntity(dto);
        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", equalTo(dto.getId())),
                hasProperty("name", equalTo(dto.getName())),
                hasProperty("description", equalTo(dto.getDescription())),
                hasProperty("available", equalTo(dto.getAvailable())),
                hasProperty("ownerId", nullValue()),
                hasProperty("request", nullValue()),
                hasProperty("comments", empty())
        ));
    }

    @Test
    void toDto_shouldMapToDto() {
        Item item = getItem(17L);

        ItemDto result = mapper.toDto(item);

        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription())),
                hasProperty("available", equalTo(item.getAvailable())),
                hasProperty("requestId", nullValue()),
                hasProperty("comments", hasSize(2))
        ));
    }

    @Test
    void toBookingDatesDto_shouldMapToDtoWithBookingDates() {
        Item item = getItem(20L);

        ItemBookingDatesDto result = mapper.toBookingDatesDto(item, TIME_BEFORE, TIME_AFTER);

        assertThat(result, allOf(
                hasProperty("id", equalTo(item.getId())),
                hasProperty("name", equalTo(item.getName())),
                hasProperty("description", equalTo(item.getDescription())),
                hasProperty("available", equalTo(item.getAvailable())),
                hasProperty("requestId", nullValue()),
                hasProperty("comments", hasSize(2)),
                hasProperty("lastBooking", notNullValue()),
                hasProperty("nextBooking", notNullValue())
        ));
    }

    @Test
    void toDto_shouldMapToListDto() {
        List<Item> items = List.of(getItem(1L), getItem(2L));

        List<ItemDto> result = mapper.toDto(items);

        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst().getId(), equalTo(items.getFirst().getId()));
        assertThat(result.getFirst().getDescription(), equalTo(items.getFirst().getDescription()));
        assertThat(result.getFirst().getName(), equalTo(items.getFirst().getName()));
    }

    @Test
    void update_shouldUpdateNotNullFields() {
        Item item = getItem(17L);
        ItemDto dto = new ItemDto();
        dto.setName("Updated");

        mapper.update(dto, item);

        assertThat(item.getName(), equalTo(dto.getName()));
        assertThat(item.getId(), equalTo(17L));
    }

    private Item getItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("item" + id);
        item.setOwnerId(id + 1);
        item.setDescription("description");
        item.setAvailable(true);
        item.setComments(List.of("comment1", "comment2"));
        return item;
    }
}