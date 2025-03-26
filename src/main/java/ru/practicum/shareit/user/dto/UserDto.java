package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.shareit.util.Marker;

@Data
public class UserDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(groups = Marker.OnCreate.class, message = "Поле name не может быть пустым")
    private String name;
    @NotBlank(groups = Marker.OnCreate.class, message = "Поле email не может быть пустым")
    @Email(message = "Поле email не соответствует формату адреса электронной почты")
    private String email;
}