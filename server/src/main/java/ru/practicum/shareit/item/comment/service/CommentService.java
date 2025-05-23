package ru.practicum.shareit.item.comment.service;

import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;

public interface CommentService {

    CommentDto create(Long userId, Long itemId, NewCommentDto commentDto);
}