package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.util.InstantMapper;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;
import static ru.practicum.shareit.TestConstant.NOT_OWNER_ID;
import static ru.practicum.shareit.TestConstant.TIME_AFTER;
import static ru.practicum.shareit.TestConstant.TIME_BEFORE;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    private final BookingService bookingService;
    private BookingDto existingBooking;
    private NewBookingDto newBookingDto;
    private UserDto booker;
    private ItemDto item;
    private final long ownerId = 11L;

    @BeforeEach
    void testInitialization() {
        booker = new UserDto();
        booker = new UserDto();
        booker.setId(12L);
        booker.setName("user2");
        booker.setEmail("user12@yandex.ru");

        item = new ItemDto();
        item.setId(31L);
        item.setName("item1");
        item.setDescription("description1");
        item.setAvailable(true);
        item.setComments(List.of("test comment"));

        existingBooking = new BookingDto();
        existingBooking.setId(41L);
        existingBooking.setStatus(BookingStatus.APPROVED);
        existingBooking.setBooker(booker);
        existingBooking.setItem(item);

        newBookingDto = new NewBookingDto();
        newBookingDto.setStart(InstantMapper.mapInstantToString(TIME_BEFORE));
        newBookingDto.setEnd(InstantMapper.mapInstantToString(TIME_AFTER));
        newBookingDto.setItemId(item.getId());
    }

    @Test
    void addBooking_withValidFields_thenReturnResult() {

        BookingDto result = bookingService.addBooking(booker.getId(), newBookingDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(1L));
        assertThat(result.getItem().getName(), equalTo(item.getName()));
        assertThat(result.getStatus(), equalTo(BookingStatus.WAITING));
    }

    @Test
    void addBooking_withNotExistingUserId_thenThrowNotFoundException() {
        assertThrows((NotFoundException.class), () -> bookingService.addBooking(NOT_EXISTING_ID, newBookingDto));
    }

    @Test
    void addBooking_withNotExistingItemId_thenThrowNotFoundException() {
        newBookingDto.setItemId(NOT_EXISTING_ID);
        assertThrows((NotFoundException.class), () -> bookingService.addBooking(booker.getId(), newBookingDto));
    }

    @Test
    void addBooking_withUserIdIsOwnerId_thenThrowNoAccessException() {
        assertThrows((NoAccessException.class), () -> bookingService.addBooking(ownerId, newBookingDto));
    }

    @Test
    void addBooking_withNotAvailableItem_thenThrowValidationException() {
        long notAvailableItemId = 33L;
        newBookingDto.setItemId(notAvailableItemId);
        assertThrows((NotAvailableException.class), () -> bookingService.addBooking(booker.getId(), newBookingDto));
    }

    @Test
    void changeStatus_withUserIdIsOwnerIdAndBookingExist_thenReturnResult() {
        existingBooking.setStatus(BookingStatus.REJECTED);
        BookingDto result = bookingService.changeStatus(ownerId, existingBooking.getId(), false);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(existingBooking.getId()));
        assertThat(result.getStatus(), equalTo(existingBooking.getStatus()));
    }

    @Test
    void changeStatus_withUserIdIsNotOwnerId_thenThrowNoAccessException() {

        assertThrows((NoAccessException.class),
                () -> bookingService.changeStatus(booker.getId(), existingBooking.getId(), false));
    }

    @Test
    void changeStatus_withNotExistingBooking_thenThrowNotFoundException() {
        assertThrows((NotFoundException.class), () -> bookingService.changeStatus(booker.getId(), NOT_EXISTING_ID, false));
    }

    @Test
    void getById_withValidId_thenReturnResult() {

        BookingDto result = bookingService.getById(ownerId, existingBooking.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(existingBooking.getId()));
        assertThat(result.getStatus(), equalTo(existingBooking.getStatus()));
        assertThat(result.getBooker(), equalTo(booker));
        assertThat(result.getItem(), equalTo(item));
    }

    @Test
    void getById_withNotOwnerOrBookerId_thenThrowNoAccessException() {
        assertThrows((NoAccessException.class), () -> bookingService.getById(NOT_OWNER_ID, existingBooking.getId()));
    }

    @Test
    void getById_withNotExistingBookingId_thenThrowNotFoundException() {
        assertThrows((NotFoundException.class), () -> bookingService.getById(booker.getId(), NOT_EXISTING_ID));
    }

    @Test
    void getByBooker_withValidBookerIdAndStateAll_thenReturnResult() {
        List<BookingDto> result = bookingService.getByBooker(booker.getId(), BookingSelectionState.ALL);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(5));
    }

    @Test
    void getByBooker_withValidBookerIdAndStateCURRENT_thenReturnResult() {
        List<BookingDto> result = bookingService.getByBooker(booker.getId(), BookingSelectionState.CURRENT);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
    }

    @Test
    void getByBooker_withValidBookerIdAndStatePAST_thenReturnResult() {
        List<BookingDto> result = bookingService.getByBooker(booker.getId(), BookingSelectionState.PAST);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void getByBooker_withValidBookerIdAndStateFUTURE_thenReturnResult() {
        List<BookingDto> result = bookingService.getByBooker(booker.getId(), BookingSelectionState.FUTURE);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
    }

    @Test
    void getByBooker_withValidBookerIdAndStateWAITING_thenReturnResult() {
        List<BookingDto> result = bookingService.getByBooker(booker.getId(), BookingSelectionState.WAITING);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void getByBooker_withValidBookerIdAndStateREJECTED_thenReturnResult() {
        List<BookingDto> result = bookingService.getByBooker(booker.getId(), BookingSelectionState.REJECTED);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void getByOwner_withValidOwnerIdAndStateCURRENT_thenReturnResult() {

        List<BookingDto> result = bookingService.getByOwner(ownerId, BookingSelectionState.CURRENT);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
    }

    @Test
    void getByOwner_withValidOwnerIdAndStatePAST_thenReturnResult() {

        List<BookingDto> result = bookingService.getByOwner(ownerId, BookingSelectionState.PAST);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst().getId(), equalTo(existingBooking.getId()));
    }

    @Test
    void getByOwner_withValidOwnerIdAndStateFUTURE_thenReturnResult() {

        List<BookingDto> result = bookingService.getByOwner(ownerId, BookingSelectionState.FUTURE);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(3));
    }

    @Test
    void getByOwner_withValidOwnerIdAndStateWAITING_thenReturnResult() {

        List<BookingDto> result = bookingService.getByOwner(ownerId, BookingSelectionState.WAITING);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void getByOwner_withValidOwnerIdAndStateREJECTED_thenReturnResult() {

        List<BookingDto> result = bookingService.getByOwner(ownerId, BookingSelectionState.REJECTED);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
    }

    @Test
    void getByOwner_withValidOwnerIdAndState_thenReturnResult() {

        List<BookingDto> result = bookingService.getByOwner(ownerId, BookingSelectionState.ALL);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(6));
    }
}