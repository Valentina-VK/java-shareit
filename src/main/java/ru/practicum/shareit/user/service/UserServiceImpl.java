package ru.practicum.shareit.user.service;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;


    @Override
    public List<UserDto> getAll() {
        return UserMapper.toDto(userRepository.getAll());
    }

    @Override
    public UserDto get(Long userId) {
        return UserMapper.toDto(userRepository.get(userId));
    }

    @Override
    public UserDto save(UserDto newUser) {
        return UserMapper.toDto(userRepository.save(UserMapper.toEntity(newUser)));
    }

    @Override
    public UserDto update(Long userId, UserDto user) {
        userRepository.get(userId);
        if (user.getEmail() != null && !isValidEmail(user.getEmail()))
            throw new ValidationException("Некорректный email");
        User updatedUser = UserMapper.toEntity(user);
        updatedUser.setId(userId);
        return UserMapper.toDto(userRepository.update(updatedUser));
    }

    @Override
    public void delete(Long userId) {
        userRepository.delete(userId);
    }

    private boolean isValidEmail(String email) {

        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@" +
                            "(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";

        Pattern pattern = Pattern.compile(emailRegex);
        return email != null && pattern.matcher(email).matches();
    }
}