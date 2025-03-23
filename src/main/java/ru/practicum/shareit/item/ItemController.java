package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.service.ItemService;
import java.util.List;

import static ru.practicum.shareit.util.HttpHeader.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(USER_ID)
                                @Positive(message = "Id пользователя должен быть положительным числом") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader(USER_ID)
                       @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                       @PathVariable
                       @Positive(message = "Id вещи должен быть положительным числом") Long itemId) {
        return itemService.get(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_ID)
                                @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                                @RequestParam(name = "text") String text) {
        return itemService.search(userId, text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_ID)
                       @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                       @Valid @RequestBody ItemDto newItem) {
        return itemService.save(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID)
                          @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                          @PathVariable
                          @Positive(message = "Id вещи должен быть положительным числом") Long itemId,
                          @RequestBody ItemDto item) {
        return itemService.update(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_ID)
                       @Positive(message = "Id пользователя должен быть положительным числом") Long userId,
                       @PathVariable
                       @Positive(message = "Id вещи должен быть положительным числом") Long itemId) {
        itemService.delete(userId, itemId);
    }
}