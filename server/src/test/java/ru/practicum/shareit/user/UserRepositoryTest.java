package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Transactional
@DataJpaTest
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;
    private User user;

    @BeforeEach
    void initBase() {
        user = new User();
        user.setName("TestName");
        user.setEmail("test@yandex.ru");
        user = userRepository.save(user);
    }

    @Test
    void save_withUniqueEmail() {
        User newUser = new User();
        newUser.setName("TestName2");
        newUser.setEmail("test@yandex.ru");
        assertThrows((DataIntegrityViolationException.class), () -> userRepository.save(newUser));
    }

    @Test
    void save_whenUpdate_ReturnUpdateFields() {
        User newUser = new User();
        newUser.setId(user.getId());
        newUser.setName("TestName2");
        newUser.setEmail("test@yandex.ru");

        User result = userRepository.save(newUser);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(newUser.getEmail()));
        assertThat(userRepository.findAll().size(), equalTo(1));
        assertThat(userRepository.findAll().getFirst(), equalTo(result));
    }

    @Test
    void findByEmail_whenEmailExist_ReturnRightResult() {
        Optional<User> result = userRepository.findByEmail(user.getEmail());

        assertTrue(result.isPresent());
        assertThat(result.get(), equalTo(user));
    }

    @Test
    void findByEmail_whenEmailNotExist_ReturnNull() {
        Optional<User> result = userRepository.findByEmail("NotExistingEmail@yandex.ru");

        assertTrue(result.isEmpty());
    }

}