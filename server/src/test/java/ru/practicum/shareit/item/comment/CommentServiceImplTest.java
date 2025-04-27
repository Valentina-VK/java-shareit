package ru.practicum.shareit.item.comment;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.item.comment.dto.CommentDto;
import ru.practicum.shareit.item.comment.dto.NewCommentDto;
import ru.practicum.shareit.item.comment.service.CommentService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.core.AllOf.allOf;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class CommentServiceImplTest {
    private final CommentService commentService;
    private NewCommentDto newCommentDto;
    private final long bookerId = 12L;

    @BeforeEach
    void testInitialization() {
        newCommentDto = new NewCommentDto();
        newCommentDto.setText("new comment");
    }

    @Test
    void create_withUserIdIsBookerIdAndBookingInPast_thenReturnResult() {
        long itemBookedInPast = 31;

        CommentDto result = commentService.create(bookerId, itemBookedInPast, newCommentDto);

        assertThat(result, allOf(
                hasProperty("id", equalTo(1L)),
                hasProperty("authorName", equalTo("user2")),
                hasProperty("text", equalTo(newCommentDto.getText())),
                hasProperty("created", notNullValue())
        ));
    }

    @Test
    void create_withUserIdIsBookerIdAndNotBookingInPast_thenThrowNotAvailableException() {
        long itemWithCurrentBooking = 32;
        assertThrows((NotAvailableException.class),
                () -> commentService.create(bookerId, itemWithCurrentBooking, newCommentDto));
    }
}