package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.NewCommentDto;

import static ru.practicum.shareit.util.Constants.ERROR_USER_ID;
import static ru.practicum.shareit.util.Constants.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;

    @GetMapping
    public ResponseEntity<Object> getAll(@RequestHeader(USER_ID)
                                         @Positive(message = ERROR_USER_ID) Long userId) {
        log.info("Get all Items by owner id: {}", userId);
        return itemClient.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader(USER_ID)
                                      @Positive(message = ERROR_USER_ID) Long userId,
                                      @PathVariable @Positive(message = "Id вещи должен быть положительным числом") Long itemId) {
        log.info("Get item by id: {}, user id: {}", itemId, userId);
        return itemClient.getById(userId, itemId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestHeader(USER_ID)
                                         @Positive(message = ERROR_USER_ID) Long userId,
                                         @RequestParam(name = "text", required = false) String text) {
        log.info("Get item by text: {}, user id: {}", text, userId);
        return itemClient.search(userId, text);
    }

    @PostMapping
    public ResponseEntity<Object> add(@RequestHeader(USER_ID)
                                      @Positive(message = ERROR_USER_ID) Long userId,
                                      @Valid @RequestBody ItemDto newItem) {
        log.info("Create item by owner id: {}", userId);
        return itemClient.save(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@RequestHeader(USER_ID)
                                         @Positive(message = ERROR_USER_ID) Long userId,
                                         @PathVariable @Positive(message = "Id вещи должен быть положительным числом") Long itemId,
                                         @RequestBody ItemDto item) {
        log.info("Update item by id: {}, user id: {}, new item: {}", itemId, userId, item);
        return itemClient.update(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Object> delete(@RequestHeader(USER_ID)
                                         @Positive(message = ERROR_USER_ID) Long userId,
                                         @PathVariable @Positive(message = "Id вещи должен быть положительным числом") Long itemId) {
        log.info("Delete item by id: {}, user id: {}", itemId, userId);
        itemClient.deleteById(userId, itemId);
        return new ResponseEntity<Object>(HttpStatus.OK);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> create(@RequestHeader(USER_ID) Long userId,
                                         @PathVariable(name = "id") Long itemId,
                                         @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Create comment: {} for item by id: {}, user id: {}", newCommentDto, itemId, userId);
        return itemClient.create(userId, itemId, newCommentDto);
    }
}