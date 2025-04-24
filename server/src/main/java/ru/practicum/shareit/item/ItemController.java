package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
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
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;
import ru.practicum.shareit.item.dto.ItemBookingDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

import static ru.practicum.shareit.util.Constants.USER_ID;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;
    private final CommentService commentService;

    @GetMapping
    public List<ItemDto> getAll(@RequestHeader(USER_ID) Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/{itemId}")
    public ItemBookingDatesDto get(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId) {
        return itemService.get(userId, itemId);
    }

    @GetMapping("/search")
    public List<ItemDto> search(@RequestHeader(USER_ID) Long userId,
                                @RequestParam(name = "text", required = false) String text) {
        return itemService.search(userId, text);
    }

    @PostMapping
    public ItemDto add(@RequestHeader(USER_ID) Long userId, @RequestBody ItemDto newItem) {
        return itemService.save(userId, newItem);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId,
                          @RequestBody ItemDto item) {
        return itemService.update(userId, itemId, item);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@RequestHeader(USER_ID) Long userId, @PathVariable Long itemId) {
        itemService.delete(userId, itemId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto create(@RequestHeader(USER_ID) Long userId,
                             @PathVariable(name = "id") Long itemId,
                             @RequestBody NewCommentDto newCommentDto) {
        return commentService.create(userId, itemId, newCommentDto);
    }
}