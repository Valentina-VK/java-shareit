package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validateService.ValidateService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ValidateService validateService;
    private final UserMapper mapper;

    @Override
    public List<UserDto> getAll() {
        return mapper.toDto(userRepository.findAll());
    }

    @Override
    public UserDto get(Long userId) {
        return mapper.toDto(validateService.checkUser(userId));
    }

    @Override
    public UserDto save(UserDto newUser) {
        return mapper.toDto(userRepository.save(mapper.toEntity(newUser)));
    }

    @Override
    public UserDto update(Long userId, UserDto user) {
        User oldUser = validateService.checkUser(userId);
        if (!oldUser.getEmail().equals(user.getEmail())) {
            if (userRepository.findByEmail(user.getEmail()).isPresent())
                throw new NotUniqueEmailException("Указанный email уже зарегистрирован");
        }
        mapper.update(user, oldUser);
        return mapper.toDto(userRepository.save(oldUser));
    }

    @Override
    public void delete(Long userId) {
        userRepository.deleteById(userId);
    }
}