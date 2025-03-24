package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {

    List<User> getAll();

    User get(Long userId);

    User save(User newUser);

    User update(User user);

    boolean isRegisteredEmail(String email);

    void delete(Long userId);
}