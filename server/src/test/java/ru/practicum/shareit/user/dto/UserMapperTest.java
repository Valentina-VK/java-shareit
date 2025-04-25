package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

class UserMapperTest {

    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toEntity_shouldMapFromUserDto() {
        UserDto dto = new UserDto();
        dto.setId(13L);
        dto.setEmail("test13@yandex.ru");
        dto.setName("test13");

        User result = mapper.toEntity(dto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(dto.getId()));
        assertThat(result.getName(), equalTo(dto.getName()));
        assertThat(result.getEmail(), equalTo(dto.getEmail()));
    }

    @Test
    void toDto_shouldMapToDto() {
        User user = getUser(17L);

        UserDto result = mapper.toDto(user);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(user.getId()));
        assertThat(result.getName(), equalTo(user.getName()));
        assertThat(result.getEmail(), equalTo(user.getEmail()));
    }

    @Test
    void toDto_shouldMapToListOfDto() {
        List<User> users = List.of(getUser(1L), getUser(2L));

        List<UserDto> result = mapper.toDto(users);

        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst().getId(), equalTo(users.getFirst().getId()));
        assertThat(result.getFirst().getName(), equalTo(users.getFirst().getName()));
        assertThat(result.getFirst().getEmail(), equalTo(users.getFirst().getEmail()));
    }

    @Test
    void update_shouldUpdateNotNullFields() {
        User user = getUser(17L);
        UserDto dto = new UserDto();
        dto.setName("Updated");

        mapper.update(dto, user);

        assertThat(user.getName(), equalTo(dto.getName()));
        assertThat(user.getEmail(), notNullValue());
        assertThat(user.getId(), notNullValue());
    }

    private User getUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test" + id + "@yandex.ru");
        user.setName("test" + id);
        return user;
    }
}