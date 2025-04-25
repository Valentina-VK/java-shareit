package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.util.InstantMapper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.AllOf.allOf;
import static ru.practicum.shareit.TestConstant.TIME_AFTER;
import static ru.practicum.shareit.TestConstant.TIME_BEFORE;

class BookingMapperTest {
    private final BookingMapper mapper = Mappers.getMapper(BookingMapper.class);

    @Test
    void toEntity() {
        Item item = getItem(15L);
        User user = getUser(55L);
        NewBookingDto newBookingDto = new NewBookingDto();
        newBookingDto.setItemId(item.getId());
        newBookingDto.setStart(InstantMapper.mapInstantToString(TIME_BEFORE));
        newBookingDto.setEnd(InstantMapper.mapInstantToString(TIME_AFTER));

        Booking result = mapper.toEntity(newBookingDto, user, item);

        assertThat(result, allOf(
                hasProperty("id", nullValue()),
                hasProperty("start", equalTo(TIME_BEFORE)),
                hasProperty("end", equalTo(TIME_AFTER)),
                hasProperty("status", equalTo(BookingStatus.WAITING)),
                hasProperty("booker", equalTo(user)),
                hasProperty("item", equalTo(item))
        ));
    }

    @Test
    void toDto() {
        Booking booking = getBooking(11L);

        BookingDto result = mapper.toDto(booking);

        assertThat(result, allOf(
                hasProperty("id", equalTo(booking.getId())),
                hasProperty("start", equalTo(InstantMapper.mapInstantToString(booking.getStart()))),
                hasProperty("end", equalTo(InstantMapper.mapInstantToString(booking.getEnd()))),
                hasProperty("status", equalTo(booking.getStatus())),
                hasProperty("booker", notNullValue()),
                hasProperty("item", notNullValue())
        ));
    }

    @Test
    void testToDto() {
        List<Booking> bookings = List.of(getBooking(11L), getBooking(12L));

        List<BookingDto> result = mapper.toDto(bookings);

        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst().getId(), equalTo(bookings.getFirst().getId()));
        assertThat(result.getFirst().getStatus(), equalTo(bookings.getFirst().getStatus()));
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

    private User getUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setEmail("test" + id + "@yandex.ru");
        user.setName("test" + id);
        return user;
    }

    private Booking getBooking(Long id) {
        Booking booking = new Booking();
        booking.setId(id);
        booking.setItem(getItem(id + 1));
        booking.setBooker(getUser(id * 2));
        booking.setStart(TIME_BEFORE);
        booking.setEnd(TIME_AFTER);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }
}