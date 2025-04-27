package ru.practicum.shareit.item.comment.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.CommentRepository;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.CommentMapper;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validateService.ValidateService;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final ValidateService validateService;
    private final CommentRepository commentRepository;
    private final CommentMapper mapper;

    @Override
    public CommentDto create(Long userId, Long itemId, NewCommentDto commentDto) {
        User user = validateService.checkUser(userId);
        Item item = validateService.checkItem(itemId);
        if (!validateService.hasPastBooking(userId, itemId)) {
            throw new NotAvailableException("Отзывы доступны только пользователям с завершенным бронированием");
        }
        Comment newComment = mapper.toEntity(commentDto, user, item);
        Comment comment = commentRepository.saveAndFlush(newComment);
        return mapper.toDto(comment);
    }
}
