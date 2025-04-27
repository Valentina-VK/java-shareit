package ru.practicum.shareit.item.comment.dto;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.InstantMapper;

@Mapper(componentModel = "spring", uses = InstantMapper.class)
public interface CommentMapper {

    @Mapping(ignore = true, target = "id")
    Comment toEntity(NewCommentDto dto, User user, Item item);

    @Mapping(source = "comment.user.name", target = "authorName")
    CommentDto toDto(Comment comment);
}