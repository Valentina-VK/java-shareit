package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.Marker;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Slf4j
@Validated
public class UserController {
    private final UserClient userClient;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users");
        return userClient.getAll();
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable
                                      @Positive(message = "Id пользователя должен быть положительным числом") Long userId) {
        log.info("Get user by id: {}", userId);
        return userClient.getById(userId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public ResponseEntity<Object> add(@Valid @RequestBody UserDto newUser) {
        log.info("Create user: {}", newUser);
        return userClient.save(newUser);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable
                                         @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                                         @Valid @RequestBody UserDto user) {
        log.info("Update user by id: {}", userId);
        return userClient.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Object> delete(@PathVariable
                                         @Positive(message = "Id пользователя должен быть положительным числом") Long userId) {
        log.info("Delete user by id: {}", userId);
        userClient.deleteById(userId);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }
}