package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.NewItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Constants;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestControllerTest {
    @MockBean
    private final ItemRequestService requestService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    private ItemRequestDto requestDto;
    private NewItemRequestDto newRequestDto;
    private UserDto requestor;

    @BeforeEach
    void testInitialization() {
        requestor = new UserDto();
        requestor.setId(12L);

        newRequestDto = new NewItemRequestDto();
        newRequestDto.setDescription("request");

        requestDto = new ItemRequestDto();
        requestDto.setId(13L);
        requestDto.setRequestor(requestor);
        requestDto.setDescription(newRequestDto.getDescription());
        requestDto.setCreated(Instant.now());
    }

    @Nested
    @DisplayName("Tests for POST requests")
    class testPostRequests {
        @SneakyThrows
        @Test
        void create_test() {
            when(requestService.createItemRequest(requestor.getId(), newRequestDto)).thenReturn(requestDto);

            mvc.perform(post("/requests")
                            .header(Constants.USER_ID, requestor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(newRequestDto)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(requestDto)));

            verify(requestService, times(1)).createItemRequest(requestor.getId(), newRequestDto);
        }
    }

    @Nested
    @DisplayName("Tests for GET requests")
    class testGetRequests {
        @SneakyThrows
        @Test
        void getAllByUser_test() {
            when(requestService.getAllByUser(requestor.getId())).thenReturn(List.of(requestDto));

            mvc.perform(get("/requests")
                            .header(Constants.USER_ID, requestor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto))));
        }

        @SneakyThrows
        @Test
        void getAll_test() {
            when(requestService.getAll()).thenReturn(List.of(requestDto));

            mvc.perform(get("/requests/all")
                            .header(Constants.USER_ID, requestor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(List.of(requestDto))));
        }

        @SneakyThrows
        @Test
        void getById_test() {
            when(requestService.getById(requestDto.getId())).thenReturn(requestDto);

            mvc.perform(get("/requests/{requestId}", requestDto.getId())
                            .header(Constants.USER_ID, requestor.getId())
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(content().json(mapper.writeValueAsString(requestDto)));
        }
    }
}