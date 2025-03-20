package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.model.User;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> registeredEmails = new HashSet<>();
    private long lastId = 0;

    public List<User> getAll() {
        return users.values().stream().toList();
    }

    public User get(Long userId) {
        return Optional.ofNullable(users.get(userId))
                .orElseThrow(() -> new NotFoundException("Пользователь не найден, id: " + userId));
    }

    public User save(User newUser) {
        if (isRegistered(newUser.getEmail())) throw new NotUniqueEmailException("Указанный email уже зарегистрирован");
        newUser.setId(getNextId());
        users.put(newUser.getId(), newUser);
        registeredEmails.add(newUser.getEmail());
        return newUser;
    }

    public User update(User user) {
        User oldUser = users.get(user.getId());
        if (user.getName() != null && !user.getName().isBlank()) oldUser.setName(user.getName());
        if (user.getEmail() != null && !user.getEmail().isBlank()) {
            if (isRegistered(user.getEmail())) throw new NotUniqueEmailException("Указанный email уже зарегистрирован");
            oldUser.setEmail(user.getEmail());
        }
        return oldUser;
    }

    public void delete(Long userId) {
        users.remove(userId);
    }

    private long getNextId() {
        return ++lastId;
    }

    private boolean isRegistered(String email) {
        return registeredEmails.contains(email);
    }
}