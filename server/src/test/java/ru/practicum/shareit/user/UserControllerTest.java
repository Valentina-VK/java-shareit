package ru.practicum.shareit.user;

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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.nio.charset.StandardCharsets;
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

@WebMvcTest(controllers = UserController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserControllerTest {
    @MockBean
    private final UserService userService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    private UserDto userDto;

    @BeforeEach
    void testInitialization() {
        userDto = new UserDto();
        userDto.setId(1L);
        userDto.setName("TestName");
        userDto.setEmail("test@yandex.ru");
    }

    @Nested
    @DisplayName("Tests for GET requests")
    class TestGetRequests {
        @SneakyThrows
        @Test
        void getAllTest() {
            when(userService.getAll()).thenReturn(List.of(userDto));

            mvc.perform(get("/users"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()", is(1)))
                    .andExpect(content().json(mapper.writeValueAsString(List.of(userDto))));

            verify(userService).getAll();
        }

        @SneakyThrows
        @Test
        void get_withValidId_thenReturnOk() {
            when(userService.get(userDto.getId())).thenReturn(userDto);

            mvc.perform(get("/users/{userId}", userDto.getId()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(userDto.getName())))
                    .andExpect(jsonPath("$.email", is(userDto.getEmail())));

            verify(userService).get(userDto.getId());
        }

        @SneakyThrows
        @Test
        void get_withNotExistingUserId_thenReturnNotFound() {
            when(userService.get(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

            mvc.perform(get("/users/{userId}", NOT_EXISTING_ID))
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).get(NOT_EXISTING_ID);
        }
    }

    @Nested
    @DisplayName("Tests for POST requests")
    class TestPostRequests {
        @SneakyThrows
        @Test
        void add_withValidFieldsInBody_thenReturnCreated() {
            UserDto newUser = new UserDto();
            newUser.setName(userDto.getName());
            newUser.setEmail(userDto.getEmail());
            newUser.setId(null);
            when(userService.save(newUser)).thenReturn(userDto);

            mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(newUser))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(userDto.getName())))
                    .andExpect(jsonPath("$.email", is(userDto.getEmail())));

            verify(userService).save(newUser);
        }

        @SneakyThrows
        @Test
        void add_withNotUniqueEmailInBody_thenReturnConflict() {
            UserDto newUser = new UserDto();
            newUser.setName(userDto.getName());
            newUser.setEmail(userDto.getEmail());
            newUser.setId(null);
            when(userService.save(newUser)).thenThrow(DataIntegrityViolationException.class);

            mvc.perform(post("/users")
                            .content(mapper.writeValueAsString(newUser))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict());

            verify(userService).save(newUser);
        }
    }

    @Nested
    @DisplayName("Tests for PATCH requests")
    class TestPatchRequests {
        @SneakyThrows
        @Test
        void update_withValidFieldsInBody_thenReturnOk() {
            UserDto updatedUser = new UserDto();
            updatedUser.setName("NewName");
            updatedUser.setEmail("NewEmail@yandex.ru");
            UserDto expectedUser = new UserDto();
            expectedUser.setName(updatedUser.getName());
            expectedUser.setEmail(updatedUser.getEmail());
            expectedUser.setId(userDto.getId());
            when(userService.update(userDto.getId(), updatedUser)).thenReturn(expectedUser);

            mvc.perform(patch("/users/{userId}", userDto.getId())
                            .content(mapper.writeValueAsString(updatedUser))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                    .andExpect(jsonPath("$.email", is(updatedUser.getEmail())));

            verify(userService).update(userDto.getId(), updatedUser);
        }

        @SneakyThrows
        @Test
        void update_withEmptyEmailInBody_thenReturnOk() {
            UserDto updatedUser = new UserDto();
            updatedUser.setName("NewName");
            updatedUser.setEmail("");
            UserDto expectedUser = new UserDto();
            expectedUser.setName(updatedUser.getName());
            expectedUser.setEmail(userDto.getEmail());
            expectedUser.setId(userDto.getId());
            when(userService.update(userDto.getId(), updatedUser)).thenReturn(expectedUser);

            mvc.perform(patch("/users/{userId}", userDto.getId())
                            .content(mapper.writeValueAsString(updatedUser))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                    .andExpect(jsonPath("$.name", is(updatedUser.getName())))
                    .andExpect(jsonPath("$.email", is(userDto.getEmail())));

            verify(userService).update(userDto.getId(), updatedUser);
        }

        @SneakyThrows
        @Test
        void update_withNotUniqueEmailInBody_thenReturnConflict() {
            UserDto updatedUser = new UserDto();
            updatedUser.setName("NewName");
            updatedUser.setEmail("NotUniqueEmail@yandex.ru");
            when(userService.update(userDto.getId(), updatedUser)).thenThrow(NotUniqueEmailException.class);

            mvc.perform(patch("/users/{userId}", userDto.getId())
                            .content(mapper.writeValueAsString(updatedUser))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isConflict());

            verify(userService).update(userDto.getId(), updatedUser);
        }

        @SneakyThrows
        @Test
        void update_withNotExistingUserId_thenReturnNotFound() {
            UserDto updatedUser = new UserDto();
            updatedUser.setName("NewName");
            updatedUser.setEmail("NewEmail@yandex.ru");
            when(userService.update(NOT_EXISTING_ID, updatedUser)).thenThrow(NotFoundException.class);

            mvc.perform(patch("/users/{userId}", NOT_EXISTING_ID)
                            .content(mapper.writeValueAsString(updatedUser))
                            .characterEncoding(StandardCharsets.UTF_8)
                            .contentType(MediaType.APPLICATION_JSON)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());

            verify(userService).update(NOT_EXISTING_ID, updatedUser);
        }
    }

    @Nested
    @DisplayName("Tests for DELETE requests")
    class TestDeleteRequests {
        @SneakyThrows
        @Test
        void delete_withValidUserId_thenReturnOk() {

            mvc.perform(delete("/users/{userId}", userDto.getId()))
                    .andExpect(status().isOk());

            verify(userService, times(1)).delete(userDto.getId());
        }

        @SneakyThrows
        @Test
        void delete_withNotExistingUserId_thenReturnBadRequest() {
            doThrow(NotFoundException.class).when(userService).delete(NOT_EXISTING_ID);

            mvc.perform(delete("/users/{userId}", NOT_EXISTING_ID))
                    .andExpect(status().isNotFound());

            verify(userService, times(1)).delete(NOT_EXISTING_ID);
        }
    }
}