package ru.practicum.shareit.item.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private String authorName;
    private String created;
}