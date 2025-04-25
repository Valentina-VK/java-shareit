package ru.practicum.shareit.request.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.core.AllOf.allOf;

class ItemRequestMapperTest {

    private final ItemRequestMapper mapper = Mappers.getMapper(ItemRequestMapper.class);

    @Test
    void toEntity_shouldMapFromDto() {
        NewItemRequestDto newItemRequestDto = new NewItemRequestDto();
        newItemRequestDto.setDescription("New Test request");

        ItemRequest result = mapper.toEntity(newItemRequestDto);
        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", nullValue()),
                hasProperty("requestor", nullValue()),
                hasProperty("description", equalTo(newItemRequestDto.getDescription())),
                hasProperty("created", notNullValue())
        ));
    }

    @Test
    void toDto_shouldMapToDto() {
        ItemRequest request = getItemRequest(17L);

        ItemRequestDto result = mapper.toDto(request);

        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", equalTo(request.getId())),
                hasProperty("requestor", nullValue()),
                hasProperty("description", equalTo(request.getDescription())),
                hasProperty("created", notNullValue()),
                hasProperty("items", nullValue())
        ));
    }

    @Test
    void toDto_shouldMapToListDto() {

        List<ItemRequest> requests = List.of(getItemRequest(1L), getItemRequest(2L));

        List<ItemRequestDto> result = mapper.toDto(requests);

        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst().getId(), equalTo(requests.getFirst().getId()));
        assertThat(result.getFirst().getDescription(), equalTo(requests.getFirst().getDescription()));
        assertThat(result.getFirst().getCreated(), equalTo(requests.getFirst().getCreated()));
    }

    @Test
    void toDto_shouldMapToDtoWithItems() {
        ItemRequest request = getItemRequest(77L);
        ItemRequestDto result = mapper.toDto(request, List.of(getItem(1L), getItem(2L)));

        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", equalTo(request.getId())),
                hasProperty("requestor", nullValue()),
                hasProperty("description", equalTo(request.getDescription())),
                hasProperty("created", notNullValue()),
                hasProperty("items", hasSize(2))
        ));
    }

    private ItemRequest getItemRequest(Long id) {
        ItemRequest request = new ItemRequest();
        request.setId(id);
        request.setDescription("item-request" + id);
        return request;
    }

    private Item getItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("item-by-request" + id);
        item.setOwnerId(id);
        return item;
    }
}