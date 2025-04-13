package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

@Data
@EqualsAndHashCode(exclude = {"available", "request"})
public class ItemBookingDatesDto {
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Long id;
    @NotBlank(message = "Поле name не может быть пустым")
    private String name;
    @NotBlank(message = "Поле description не может быть пустым")
    private String description;
    @NotNull(message = "Поле available не может быть пустым")
    private Boolean available;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ItemRequestDto request;
    private List<String> comments;
    private String lastBooking;
    private String nextBooking;
}
