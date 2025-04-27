package ru.practicum.shareit.item;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.InstantMapper;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;
import static ru.practicum.shareit.TestConstant.NOT_OWNER_ID;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    private final EntityManager em;
    private final ItemService itemService;
    private Item existingItem;
    private ItemDto itemDto;

    @BeforeEach
    void testInitialization() {
        existingItem = new Item();
        existingItem.setId(31L);
        existingItem.setOwnerId(11L);
        existingItem.setName("item1");
        existingItem.setDescription("description1");
        existingItem.setAvailable(true);
        existingItem.setComments(List.of("test comment"));

        itemDto = new ItemDto();
        itemDto.setId(existingItem.getId());
        itemDto.setName(existingItem.getName());
        itemDto.setDescription(existingItem.getDescription());
        itemDto.setAvailable(existingItem.getAvailable());
        itemDto.setComments(existingItem.getComments());
    }

    @Nested
    @DisplayName("Tests for method - getAll")
    class testGetAll {
        @Test
        void getAll_withValidUserId_thenReturnRightResult() {

            List<ItemDto> result = itemService.getAll(existingItem.getOwnerId());

            assertThat(result.size(), equalTo(3));
            assertThat(result.getFirst(), equalTo(itemDto));
        }

        @Test
        void getAll_withNotExistingUserId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.getAll(NOT_EXISTING_ID));
        }
    }

    @Nested
    @DisplayName("Tests for method - get")
    class testGet {
        @Test
        void get_withUserIdIsOwnerIdAndItemExist_thenReturnItemWithDatesOfBooking() {
            ItemBookingDatesDto result = itemService.get(existingItem.getOwnerId(), existingItem.getId());

            assertThat(result, notNullValue());
            assertThat(result.getId(), equalTo(existingItem.getId()));
            assertTrue(InstantMapper.mapStringToInstant(result.getLastBooking()).isBefore(Instant.now()));
            assertTrue(InstantMapper.mapStringToInstant(result.getNextBooking()).isAfter(Instant.now()));
        }

        @Test
        void get_withUserIdIsNotOwnerIdAndItemExist_thenReturnItemWithDatesOfBookingNull() {
            ItemBookingDatesDto result = itemService.get(NOT_OWNER_ID, existingItem.getId());

            assertThat(result, notNullValue());
            assertThat(result.getId(), equalTo(existingItem.getId()));
            assertNull(result.getLastBooking());
            assertNull(result.getNextBooking());
        }

        @Test
        void get_withNotExistingItemId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.get(existingItem.getOwnerId(), NOT_EXISTING_ID));
        }

        @Test
        void get_withNotExistingUserId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.get(NOT_EXISTING_ID, existingItem.getId()));
        }
    }

    @Nested
    @DisplayName("Tests for method - search")
    class testSearch {
        @Test
        void search_withValidUserIdAndSuitableText_thenReturnRightResult() {
            List<ItemDto> result = itemService.search(existingItem.getOwnerId(), "item1");

            assertThat(result.size(), equalTo(1));
            assertThat(result.getFirst(), equalTo(itemDto));
            assertThat(result.getFirst().getName(), equalTo(existingItem.getName()));
        }

        @Test
        void search_withValidUserIdAndTextIsBlank_thenReturnEmptyList() {
            List<ItemDto> result = itemService.search(existingItem.getOwnerId(), "");

            assertTrue(result.isEmpty());
        }

        @Test
        void search_withNotExistingUserId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.search(NOT_EXISTING_ID, "item1"));
        }
    }

    @Nested
    @DisplayName("Tests for method - save")
    class testSave {
        @Test
        void save_withValidUserIdAndItemDto_thenReturnCreatedItem() {
            ItemDto newItem = new ItemDto();
            newItem.setName("new Item");
            newItem.setDescription("new Description");
            newItem.setAvailable(true);

            ItemDto result = itemService.save(existingItem.getOwnerId(), newItem);
            assertThat(result, allOf(
                    hasProperty("id", equalTo(1L)),
                    hasProperty("name", equalTo(newItem.getName())),
                    hasProperty("description", equalTo(newItem.getDescription())),
                    hasProperty("available", equalTo(true))
            ));
        }

        @Test
        void save_withValidUserIdAndItemDtoIsNull_thenThrowRuntimeException() {
            assertThrows((RuntimeException.class), () -> itemService.save(existingItem.getOwnerId(), null));
        }

        @Test
        void save_withVNotExistingUserId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.save(NOT_EXISTING_ID, itemDto));
        }
    }

    @Nested
    @DisplayName("Tests for method - update")
    class testUpdate {
        @Test
        void update_withUserIdIsOwnerIdAndExistingItemId_thenReturnUpdatedItem() {
            ItemDto newItem = new ItemDto();
            newItem.setDescription("new Description");
            ItemDto result = itemService.update(existingItem.getOwnerId(), existingItem.getId(), newItem);

            assertThat(result, allOf(
                    hasProperty("id", equalTo(existingItem.getId())),
                    hasProperty("name", equalTo(existingItem.getName())),
                    hasProperty("description", equalTo(newItem.getDescription())),
                    hasProperty("available", equalTo(existingItem.getAvailable()))
            ));
        }

        @Test
        void update_withUserIdIsNotOwnerIdAndExistingItemId_thenThrowNoAccessException() {
            assertThrows((NoAccessException.class), () -> itemService.update(NOT_OWNER_ID, existingItem.getId(), itemDto));
        }

        @Test
        void update_withNotExistingUserId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.update(NOT_EXISTING_ID, existingItem.getId(), itemDto));
        }

        @Test
        void update_withNotExistingItemId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.update(existingItem.getOwnerId(), NOT_EXISTING_ID, itemDto));
        }
    }

    @Nested
    @DisplayName("Tests for method - delete")
    class testDelete {
        @Test
        void delete_withUserIdIsOwnerIdAndExistingItemId_thenEndCorrectly() {
            itemService.delete(existingItem.getOwnerId(), existingItem.getId());

            TypedQuery<Item> query = em.createQuery("SELECT i FROM Item AS i", Item.class);

            assertTrue(query.getResultStream()
                    .filter(user -> user.getId().equals(existingItem.getId()))
                    .findFirst()
                    .isEmpty());
        }

        @Test
        void delete_withUserIdIsNotOwnerIdAndExistingItemId_thenThrowNoAccessException() {
            assertThrows((NoAccessException.class), () -> itemService.delete(NOT_OWNER_ID, existingItem.getId()));
        }

        @Test
        void delete_withNotExistingUserId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.delete(NOT_EXISTING_ID, existingItem.getId()));
        }

        @Test
        void delete_withNotExistingItemId_thenThrowNotFoundException() {
            assertThrows((NotFoundException.class), () -> itemService.delete(existingItem.getOwnerId(), NOT_EXISTING_ID));
        }
    }
}