package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemBookingDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;
import static ru.practicum.shareit.TestConstant.NOT_OWNER_ID;
import static ru.practicum.shareit.util.Constants.USER_ID;

@WebMvcTest(controllers = ItemController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemControllerTest {
    @MockBean
    private final ItemService itemService;
    @MockBean
    private final CommentService commentService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    private ItemDto itemDto;
    private ItemBookingDatesDto itemBookingDatesDto;
    private CommentDto commentDto;
    private NewCommentDto newCommentDto;
    private static final Long ONE_USER_ID = 1L;
    private static final Long ITEM_ID = 2L;

    @BeforeEach
    void testInitialization() {
        itemDto = new ItemDto();
        itemDto.setName("TestItem");
        itemDto.setDescription("for test");
        itemDto.setAvailable(true);
        itemDto.setComments(List.of("test comment"));

        itemBookingDatesDto = new ItemBookingDatesDto();
        itemBookingDatesDto.setId(ITEM_ID);
        itemBookingDatesDto.setName(itemDto.getName());
        itemBookingDatesDto.setDescription(itemDto.getDescription());
        itemBookingDatesDto.setAvailable(itemDto.getAvailable());
        itemBookingDatesDto.setComments(itemDto.getComments());
        itemBookingDatesDto.setLastBooking("2025-03-22'T'12:00:00");
        itemBookingDatesDto.setNextBooking("2025-05-12'T'15:00:00");

        commentDto = new CommentDto(99L, "Test comment", "Author", "2025-02-12'T'15:55:17");
        newCommentDto = new NewCommentDto();
        newCommentDto.setText("New comment");
    }

    @Nested
    @DisplayName("Tests for GET requests")
    class TestGetRequests {
        @SneakyThrows
        @Test
        void getAll_withValidUserIdInHeader_thenReturnOk() {

            when(itemService.getAll(ONE_USER_ID)).thenReturn(List.of(itemDto));

            mvc.perform(get("/items")
                            .header(USER_ID, ONE_USER_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(1)))
                    .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

            verify(itemService).getAll(ONE_USER_ID);
        }

        @SneakyThrows
        @Test
        void get_withValidItemId_thenReturnOk() {
            when(itemService.get(ONE_USER_ID, ITEM_ID)).thenReturn(itemBookingDatesDto);

            mvc.perform(get("/items/{itemId}", ITEM_ID)
                            .header(USER_ID, ONE_USER_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(itemBookingDatesDto)));

            verify(itemService).get(ONE_USER_ID, ITEM_ID);
        }

        @SneakyThrows
        @Test
        void get_withNotExistingItemId_thenReturnNotFound() {
            when(itemService.get(ONE_USER_ID, NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

            mvc.perform(get("/items/{itemId}", NOT_EXISTING_ID)
                            .header(USER_ID, ONE_USER_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(itemService).get(ONE_USER_ID, NOT_EXISTING_ID);
        }

        @SneakyThrows
        @Test
        void search_withText_thenReturnOk() {
            when(itemService.search(ONE_USER_ID, "testText")).thenReturn(List.of(itemDto));

            mvc.perform(get("/items/search")
                            .header(USER_ID, ONE_USER_ID)
                            .param("text", "testText")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(1)))
                    .andExpect(content().json(mapper.writeValueAsString(List.of(itemDto))));

            verify(itemService).search(ONE_USER_ID, "testText");
        }

        @SneakyThrows
        @Test
        void search_withoutText_thenReturnOk() {
            when(itemService.search(ONE_USER_ID, null)).thenReturn(List.of());

            mvc.perform(get("/items/search")
                            .header(USER_ID, ONE_USER_ID)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(0)))
                    .andExpect(content().json(mapper.writeValueAsString(List.of())));

            verify(itemService).search(ONE_USER_ID, null);
        }
    }

    @Nested
    @DisplayName("Tests for POST requests")
    class TestPostRequests {
        @SneakyThrows
        @Test
        void add_withValidBody_thenReturnOK() {
            when(itemService.save(ONE_USER_ID, itemDto)).thenReturn(itemDto);
            String itemInJson = mapper.writeValueAsString(itemDto);

            mvc.perform(post("/items")
                            .header(USER_ID, ONE_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(itemInJson))
                    .andExpect(status().isOk())
                    .andExpect(content().json(itemInJson));

            verify(itemService, times(1)).save(ONE_USER_ID, itemDto);
        }

        @SneakyThrows
        @Test
        void create_withUserIdIsBookerIdAndBookingInPast_thenReturnOk() {
            when(commentService.create(ONE_USER_ID, ITEM_ID, newCommentDto)).thenReturn(commentDto);

            mvc.perform(post("/items/{id}/comment", ITEM_ID)
                            .header(USER_ID, ONE_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newCommentDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(commentDto)));

            verify(commentService, times(1)).create(ONE_USER_ID, ITEM_ID, newCommentDto);
        }

        @SneakyThrows
        @Test
        void create_withUserIdIsNotBookerIdOrNotBookingInPast_thenReturnForbidden() {

            when(commentService.create(ONE_USER_ID, ITEM_ID, newCommentDto)).thenThrow(NoAccessException.class);

            mvc.perform(post("/items/{id}/comment", ITEM_ID)
                            .header(USER_ID, ONE_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newCommentDto)))
                    .andExpect(status().isForbidden());

            verify(commentService, times(1)).create(ONE_USER_ID, ITEM_ID, newCommentDto);
        }
    }

    @Nested
    @DisplayName("Tests for PATCH requests")
    class TestPatchRequests {
        @SneakyThrows
        @Test
        void update_withUserIdIsOwnerId_thenReturnOk() {
            when(itemService.update(ONE_USER_ID, ITEM_ID, itemDto)).thenReturn(itemDto);
            String itemInJson = mapper.writeValueAsString(itemDto);

            mvc.perform(patch("/items/{itemId}", ITEM_ID)
                            .header(USER_ID, ONE_USER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(itemInJson))
                    .andExpect(status().isOk())
                    .andExpect(content().json(itemInJson));

            verify(itemService, times(1)).update(ONE_USER_ID, ITEM_ID, itemDto);
        }

        @SneakyThrows
        @Test
        void update_withUserIdIsNotOwnerId_thenReturnForbidden() {
            when(itemService.update(NOT_OWNER_ID, ITEM_ID, itemDto)).thenThrow(NoAccessException.class);
            String itemInJson = mapper.writeValueAsString(itemDto);

            mvc.perform(patch("/items/{itemId}", ITEM_ID)
                            .header(USER_ID, NOT_OWNER_ID)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(itemInJson))
                    .andExpect(status().isForbidden());

            verify(itemService, times(1)).update(NOT_OWNER_ID, ITEM_ID, itemDto);
        }
    }

    @Nested
    @DisplayName("Tests for DELETE requests")
    class TestDeleteRequests {
        @SneakyThrows
        @Test
        void delete_withUserIdIsOwnerId_thenReturnOK() {

            mvc.perform(delete("/items/{itemId}", ITEM_ID)
                            .header(USER_ID, ONE_USER_ID))
                    .andExpect(status().isOk());

            verify(itemService, times(1)).delete(ONE_USER_ID, ITEM_ID);
        }

        @SneakyThrows
        @Test
        void delete_withUserIdIsNotOwnerId_thenReturnForbidden() {
            doThrow(NoAccessException.class).when(itemService).delete(NOT_OWNER_ID, ITEM_ID);

            mvc.perform(delete("/items/{itemId}", ITEM_ID)
                            .header(USER_ID, NOT_OWNER_ID))
                    .andExpect(status().isForbidden());

            verify(itemService, times(1)).delete(NOT_OWNER_ID, ITEM_ID);
        }
    }
}