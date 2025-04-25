package ru.practicum.shareit.item.comment.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;

class CommentMapperTest {

    private final CommentMapper mapper = Mappers.getMapper(CommentMapper.class);

    @Test
    void toEntity_shouldMapFromUserDto() {
        NewCommentDto commentDto = new NewCommentDto();
        commentDto.setText("new comment");
        Item item = getItem(44L);
        User user = getUser(1L);

        Comment result = mapper.toEntity(commentDto, user, item);
        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", nullValue()),
                hasProperty("user", equalTo(user)),
                hasProperty("item", equalTo(item)),
                hasProperty("text", equalTo(commentDto.getText())),
                hasProperty("created", notNullValue())
        ));
    }

    @Test
    void toDto_shouldMapToDto() {
        User user = getUser(15L);
        Comment comment = new Comment();
        comment.setId(105L);
        comment.setItem(getItem(55L));
        comment.setUser(user);
        comment.setText("comment");

        CommentDto result = mapper.toDto(comment);

        assertThat(result, notNullValue());
        assertThat(result, allOf(
                hasProperty("id", equalTo(comment.getId())),
                hasProperty("authorName", equalTo(user.getName())),
                hasProperty("text", equalTo(comment.getText())),
                hasProperty("created", notNullValue())
        ));
    }

    private User getUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test" + id + "@yandex.ru");
        user.setName("test" + id);
        return user;
    }

    private Item getItem(Long id) {
        Item item = new Item();
        item.setId(id);
        item.setName("item" + id);
        item.setOwnerId(id + 1);
        item.setDescription("description");
        item.setAvailable(true);
        item.setComments(List.of("comment1", "comment2"));
        return item;
    }
}