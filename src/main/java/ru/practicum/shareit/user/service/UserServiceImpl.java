package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getAll() {
        return mapper.toDto(userRepository.getAll());
    }

    @Override
    public UserDto get(Long userId) {
        return mapper.toDto(userRepository.get(userId));
    }

    @Override
    public UserDto save(UserDto newUser) {
        return mapper.toDto(userRepository.save(mapper.toEntity(newUser)));
    }

    @Override
    public UserDto update(Long userId, UserDto user) {
        userRepository.get(userId);
        User updatedUser = mapper.toEntity(user);
        updatedUser.setId(userId);
        return mapper.toDto(userRepository.update(updatedUser));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }
}