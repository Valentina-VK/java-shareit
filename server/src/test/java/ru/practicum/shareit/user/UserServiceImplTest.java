package ru.practicum.shareit.user;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final EntityManager em;
    private final UserService service;
    private UserDto newUser;
    private UserDto existingUser;

    @BeforeEach
    void testInitialization() {
        existingUser = new UserDto();
        existingUser.setId(12L);
        existingUser.setName("user2");
        existingUser.setEmail("user12@yandex.ru");

        newUser = new UserDto();
        newUser.setId(1L);
        newUser.setName("TestName");
        newUser.setEmail("test@yandex.ru");
    }

    @Nested
    @DisplayName("Tests for method - getAll")
    class testGetAll {
        @Test
        void getAllTest() {

            List<UserDto> result = service.getAll();

            assertThat(result.size(), equalTo(3));
            assertTrue(result.contains(existingUser));
        }
    }

    @Nested
    @DisplayName("Tests for method - get")
    class testGet {

        @Test
        void get_withValidId_thenRightResult() {
            UserDto result = service.get(existingUser.getId());

            assertThat(result, notNullValue());
            assertThat(result.getId(), equalTo(existingUser.getId()));
            assertThat(result.getName(), equalTo(existingUser.getName()));
            assertThat(result.getEmail(), equalTo(existingUser.getEmail()));
        }

        @Test
        void get_withNotExistingId_thenThrowNotFoundException() {

            assertThrows((NotFoundException.class), () -> service.get(NOT_EXISTING_ID));
        }
    }

    @Nested
    @DisplayName("Tests for method - save")
    class testSave {

        @Test
        void save_withValidFields_thenRightResult() {

            UserDto result = service.save(newUser);

            assertThat(result, notNullValue());
            assertThat(result.getId(), equalTo(newUser.getId()));
            assertThat(result.getName(), equalTo(newUser.getName()));
            assertThat(result.getEmail(), equalTo(newUser.getEmail()));
        }

        @Test
        void save_withNotUniqueEmail_thenRightResult() {
            newUser.setEmail(existingUser.getEmail());

            assertThrows((DataIntegrityViolationException.class), () -> service.save(newUser));
        }
    }

    @Nested
    @DisplayName("Tests for method - update")
    class testUpdate {

        @Test
        void update_withValidFields_thenRightResult() {
            UserDto updatedUserdto = new UserDto();
            updatedUserdto.setEmail("NewEmail@yandex.ru");

            UserDto result = service.update(existingUser.getId(), updatedUserdto);

            assertThat(result, notNullValue());
            assertThat(result.getId(), equalTo(existingUser.getId()));
            assertThat(result.getName(), equalTo(existingUser.getName()));
            assertThat(result.getEmail(), equalTo(updatedUserdto.getEmail()));
        }

        @Test
        void update_withNotExistingUserId_thenReturnNotFoundException() {
            UserDto updatedUserdto = new UserDto();
            updatedUserdto.setEmail("NewEmail@yandex.ru");

            assertThrows((NotFoundException.class), () -> service.update(NOT_EXISTING_ID, updatedUserdto));
        }

        @Test
        void update_withNotUniqueEmail_thenRightResult() {
            String existingEmail = "user11@yandex.ru";
            UserDto updatedUserdto = new UserDto();
            updatedUserdto.setEmail(existingEmail);

            assertThrows((NotUniqueEmailException.class),
                    () -> service.update(existingUser.getId(), updatedUserdto));
        }
    }

    @Nested
    @DisplayName("Tests for method - delete")
    class testDelete {

        @Test
        void delete_withAnyLongUserId_thenReturnOk() {
            service.delete(existingUser.getId());

            TypedQuery<User> query = em.createQuery("SELECT u FROM User AS u", User.class);

            assertTrue(query.getResultStream()
                    .filter(user -> user.getId().equals(existingUser.getId()))
                    .findFirst()
                    .isEmpty());
        }
    }
}