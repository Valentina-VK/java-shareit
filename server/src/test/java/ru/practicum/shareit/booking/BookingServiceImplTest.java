package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.selection.SelectionService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validateService.ValidateService;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;
import static ru.practicum.shareit.TestConstant.NOT_OWNER_ID;
import static ru.practicum.shareit.TestConstant.TIME_AFTER;
import static ru.practicum.shareit.TestConstant.TIME_BEFORE;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ValidateService validateService;
    @Mock
    private BookingMapper mapper;
    @Mock
    private SelectionService selectionService;
    @InjectMocks
    private BookingServiceImpl bookingService;
    private Item item;
    private ItemDto itemDto;
    private Booking booking;
    private BookingDto bookingDto;
    private NewBookingDto newBookingDto;
    private User booker;

    @BeforeEach
    void testInitialization() {
        booker = new User();
        booker.setId(12L);

        item = new Item();
        item.setId(1L);
        item.setOwnerId(10L);
        item.setAvailable(true);

        itemDto = new ItemDto();
        itemDto.setId(item.getId());

        booking = new Booking();
        booking.setId(111L);
        booking.setStart(TIME_BEFORE);
        booking.setEnd(TIME_AFTER);
        booking.setStatus(BookingStatus.WAITING);
        booking.setBooker(booker);
        booking.setItem(item);

        bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStart(TIME_BEFORE.toString());
        bookingDto.setEnd(TIME_AFTER.toString());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(new UserDto());
        bookingDto.setItem(itemDto);

        newBookingDto = new NewBookingDto();
        newBookingDto.setStart(TIME_BEFORE.toString());
        newBookingDto.setEnd(TIME_AFTER.toString());
        newBookingDto.setItemId(item.getId());
    }

    @Test
    void addBooking_withValidFields_thenReturnResult() {
        when(validateService.checkUser(booker.getId())).thenReturn(booker);
        when(validateService.checkItem(newBookingDto.getItemId())).thenReturn(item);
        when(mapper.toEntity(newBookingDto, booker, item)).thenReturn(booking);
        when(mapper.toDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.addBooking(booker.getId(), newBookingDto);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingDto.getId()));
        assertThat(result.getItem().getName(), equalTo(item.getName()));

        verify(validateService, times(1)).checkUser(booker.getId());
        verify(validateService, times(1)).checkItem(newBookingDto.getItemId());
        verify(bookingRepository, times(1)).save(booking);
        verify(mapper, times(1)).toEntity(newBookingDto, booker, item);
        verify(mapper, times(1)).toDto(booking);
    }

    @Test
    void addBooking_withNotExistingUserId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> bookingService.addBooking(NOT_EXISTING_ID, newBookingDto));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void addBooking_withNotExistingItemId_thenThrowNotFoundException() {
        newBookingDto.setItemId(NOT_EXISTING_ID);
        when(validateService.checkItem(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> bookingService.addBooking(booker.getId(), newBookingDto));

        verify(validateService, times(1)).checkItem(NOT_EXISTING_ID);
        verify(validateService, times(1)).checkUser(anyLong());
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void addBooking_withUserIdIsOwnerId_thenThrowNoAccessException() {
        when(validateService.checkItem(newBookingDto.getItemId())).thenReturn(item);

        assertThrows((NoAccessException.class), () -> bookingService.addBooking(item.getOwnerId(), newBookingDto));

        verify(validateService, times(1)).checkItem(newBookingDto.getItemId());
        verify(validateService, times(1)).checkUser(anyLong());
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void addBooking_withNotAvailableItem_thenThrowValidationException() {
        item.setAvailable(false);
        when(validateService.checkItem(newBookingDto.getItemId())).thenReturn(item);

        assertThrows((NotAvailableException.class), () -> bookingService.addBooking(booker.getId(), newBookingDto));

        verify(validateService, times(1)).checkItem(newBookingDto.getItemId());
        verify(validateService, times(1)).checkUser(anyLong());
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void changeStatus_withUserIdIsOwnerIdAndBookingExist_thenReturnResult() {
        bookingDto.setStatus(BookingStatus.REJECTED);
        when(validateService.checkBooking(booking.getId())).thenReturn(booking);
        when(mapper.toDto(booking)).thenReturn(bookingDto);
        when(bookingRepository.save(booking)).thenReturn(booking);

        BookingDto result = bookingService.changeStatus(item.getOwnerId(), booking.getId(), false);

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingDto.getId()));
        assertThat(result.getStatus(), equalTo(bookingDto.getStatus()));

        verify(validateService, times(1)).checkBooking(booking.getId());
        verify(bookingRepository, times(1)).save(booking);
        verify(mapper, times(1)).toDto(booking);
    }

    @Test
    void changeStatus_withUserIdIsNotOwnerId_thenThrowNoAccessException() {
        when(validateService.checkBooking(booking.getId())).thenReturn(booking);

        assertThrows((NoAccessException.class), () -> bookingService.changeStatus(booker.getId(), booking.getId(), false));

        verify(validateService, times(1)).checkBooking(booking.getId());
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void changeStatus_withNotExistingBooking_thenThrowNotFoundException() {
        when(validateService.checkBooking(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> bookingService.changeStatus(booker.getId(), NOT_EXISTING_ID, false));

        verify(validateService, times(1)).checkBooking(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);

    }

    @Test
    void getById_withValidId_thenReturnResult() {
        when(validateService.checkBooking(booking.getId())).thenReturn(booking);
        when(mapper.toDto(booking)).thenReturn(bookingDto);

        BookingDto result = bookingService.getById(item.getOwnerId(), booking.getId());

        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(bookingDto.getId()));
        assertThat(result.getStatus(), equalTo(bookingDto.getStatus()));

        verify(validateService, times(1)).checkBooking(booking.getId());
        verify(mapper, times(1)).toDto(booking);
    }

    @Test
    void getById_withNotOwnerOrBookerId_thenThrowNoAccessException() {
        when(validateService.checkBooking(booking.getId())).thenReturn(booking);

        assertThrows((NoAccessException.class), () -> bookingService.getById(NOT_OWNER_ID, booking.getId()));

        verify(validateService, times(1)).checkBooking(booking.getId());
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void getById_withNotExistingBookingId_thenThrowNotFoundException() {
        when(validateService.checkBooking(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> bookingService.getById(booker.getId(), NOT_EXISTING_ID));

        verify(validateService, times(1)).checkBooking(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void getByBooker_withValidBookerId_thenReturnResult() {
        when(selectionService.handleByBooker(anyLong(), any(BookingSelectionState.class))).thenReturn(List.of(booking));
        when(mapper.toDto(List.of(booking))).thenReturn(List.of(bookingDto));

        List<BookingDto> result = bookingService.getByBooker(booker.getId(), BookingSelectionState.ALL);

        assertThat(result, notNullValue());
        assertThat(result.getFirst(), equalTo(bookingDto));

        verify(selectionService, times(1)).handleByBooker(anyLong(), any(BookingSelectionState.class));
        verify(mapper, times(1)).toDto(List.of(booking));
    }

    @Test
    void getByOwner_withValidOwnerId_thenReturnResult() {
        when(selectionService.handleByOwner(anyLong(), any(BookingSelectionState.class))).thenReturn(List.of(booking));
        when(mapper.toDto(List.of(booking))).thenReturn(List.of(bookingDto));

        List<BookingDto> result = bookingService.getByOwner(booker.getId(), BookingSelectionState.ALL);

        assertThat(result, notNullValue());
        assertThat(result.getFirst(), equalTo(bookingDto));

        verify(selectionService, times(1)).handleByOwner(anyLong(), any(BookingSelectionState.class));
        verify(mapper, times(1)).toDto(List.of(booking));
    }
}