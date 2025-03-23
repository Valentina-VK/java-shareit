package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.util.Marker;

import java.util.List;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@Validated
public class UserController {
    private final UserService userService;

    @GetMapping
    public List<UserDto> getAll() {
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable
                       @Positive(message = "Id пользователя должен быть положительным числом") Long userId) {
        return userService.get(userId);
    }

    @PostMapping
    @Validated({Marker.OnCreate.class})
    public UserDto add(@Valid @RequestBody UserDto newUser) {
        return userService.save(newUser);
    }

    @PatchMapping("/{userId}")
    public UserDto update(@PathVariable
                          @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                          @Valid @RequestBody UserDto user) {
        return userService.update(userId, user);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable
                       @Positive(message = "Id пользователя должен быть положительным числом") Long userId) {
        userService.delete(userId);
    }
}