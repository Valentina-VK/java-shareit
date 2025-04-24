package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotUniqueEmailException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;
import ru.practicum.shareit.validateService.ValidateService;

import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private ValidateService validateService;
    @Mock
    private UserMapper mapper;
    @InjectMocks
    private UserServiceImpl userService;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void testInitialization() {
        user = new User();
        user.setId(1L);
        user.setName("TestName");
        user.setEmail("test@yandex.ru");

        userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setName(user.getName());
        userDto.setEmail(user.getEmail());
    }


    @Test
    void getAllTest() {
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(mapper.toDto(List.of(user))).thenReturn(List.of(userDto));

        List<UserDto> result = userService.getAll();
        assertThat(result.size(), equalTo(1));

        assertThat(result.getFirst(), equalTo(userDto));

        verify(userRepository, times(1)).findAll();
        verify(mapper, times(1)).toDto(List.of(user));
    }

    @Test
    void get_withValidId_thenRightResult() {
        when(validateService.checkUser(user.getId())).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.get(user.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));

        verify(validateService, times(1)).checkUser(user.getId());
        verify(mapper, times(1)).toDto(user);
    }

    @Test
    void get_withNotExistingId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> userService.get(NOT_EXISTING_ID));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verify(mapper, never()).toDto(user);
    }

    @Test
    void save_withValidFields_thenRightResult() {
        when(mapper.toEntity(userDto)).thenReturn(user);
        when(mapper.toDto(user)).thenReturn(userDto);
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.save(userDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(userDto.getId()));
        assertThat(result.getName(), equalTo(userDto.getName()));
        assertThat(result.getEmail(), equalTo(userDto.getEmail()));

        verify(userRepository, times(1)).save(user);
        verify(mapper, times(1)).toDto(user);
        verify(mapper, times(1)).toEntity(userDto);
    }

    @Test
    void save_withNotUniqueEmail_thenRightResult() {
        when(mapper.toEntity(userDto)).thenReturn(user);
        when(userRepository.save(user)).thenThrow(DataIntegrityViolationException.class);

        assertThrows((DataIntegrityViolationException.class), () -> userService.save(userDto));

        verify(userRepository, times(1)).save(user);
        verify(mapper, never()).toDto(user);
        verify(mapper, times(1)).toEntity(userDto);
    }

    @Test
    void update_withValidFields_thenRightResult() {
        UserDto updatedUserdto = new UserDto();
        updatedUserdto.setEmail("NewEmail@yandex.ru");
        User expectedUser = new User();
        expectedUser.setId(user.getId());
        expectedUser.setEmail(updatedUserdto.getEmail());
        expectedUser.setName(user.getName());
        userDto.setEmail(expectedUser.getEmail());
        when(validateService.checkUser(user.getId())).thenReturn(user);
        when(userRepository.findByEmail(updatedUserdto.getEmail())).thenReturn(Optional.empty());

        when(userRepository.save(expectedUser)).thenReturn(expectedUser);
        when(mapper.toDto(expectedUser)).thenReturn(userDto);

        UserDto result = userService.update(user.getId(), updatedUserdto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(expectedUser.getId()));
        assertThat(result.getName(), equalTo(expectedUser.getName()));
        assertThat(result.getEmail(), equalTo(expectedUser.getEmail()));

        verify(validateService, times(1)).checkUser(user.getId());
        verify(userRepository, times(1)).findByEmail(updatedUserdto.getEmail());
        verify(mapper, times(1)).update(updatedUserdto, user);
        verify(userRepository, times(1)).save(expectedUser);
        verify(mapper, times(1)).toDto(expectedUser);
    }

    @Test
    void update_withNotExistingUserId_thenReturnNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class),
                () -> userService.update(NOT_EXISTING_ID, any(UserDto.class)));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verify(userRepository, never()).findByEmail(any());
        verify(mapper, never()).update(any(), any());
        verify(userRepository, never()).save(any());
        verify(mapper, never()).toDto(any(User.class));
    }

    @Test
    void update_withNotUniqueEmail_thenRightResult() {
        UserDto updatedUserdto = new UserDto();
        updatedUserdto.setEmail("NotUniqueEmail@yandex.ru");
        when(validateService.checkUser(user.getId())).thenReturn(user);
        when(userRepository.findByEmail(updatedUserdto.getEmail())).thenReturn(Optional.of(new User()));

        assertThrows((NotUniqueEmailException.class),
                () -> userService.update(user.getId(), updatedUserdto));

        verify(validateService, times(1)).checkUser(user.getId());
        verify(userRepository, times(1)).findByEmail(updatedUserdto.getEmail());
        verify(mapper, never()).update(any(), any());
        verify(userRepository, never()).save(any());
        verify(mapper, never()).toDto(any(User.class));
    }


    @Test
    void delete_withAnyLongUserId_thenReturnOk() {
        assertDoesNotThrow(() -> userService.delete(anyLong()));

        verify(userRepository, times(1)).deleteById(anyLong());
    }
}