package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getAll();

    UserDto get(Long userId);

    UserDto save(UserDto newUser);

    UserDto update(Long userId, UserDto user);

    void delete(Long userId);
}