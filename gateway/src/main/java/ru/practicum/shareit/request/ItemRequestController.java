package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.request.dto.NewItemRequestDto;

import static ru.practicum.shareit.util.Constants.USER_ID;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(USER_ID)
                                         @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                                         @Valid @RequestBody NewItemRequestDto newItemRequestDto) {
        log.info("Create request on item, user id: {}", userId);
        return itemRequestClient.createItemRequest(userId, newItemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader(USER_ID)
                                               @Positive(message = "Id пользователя должен быть положительным числом") Long userId) {
        log.info("Get all requests on items, by user id: {}", userId);
        return itemRequestClient.getAllByUser(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll() {
        log.info("Get all requests on item");
        return itemRequestClient.getAll();
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getById(@PathVariable Long requestId) {
        log.info("Get request on item by id: {}", requestId);
        return itemRequestClient.getById(requestId);
    }
}