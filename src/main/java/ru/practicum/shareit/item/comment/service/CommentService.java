package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;

public interface CommentService {

    CommentDto create(Long itemId, Long userId, NewCommentDto commentDto);
}
