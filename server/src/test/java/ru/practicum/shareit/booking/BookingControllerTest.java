package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.NewBookingDto;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;
import static ru.practicum.shareit.TestConstant.TIME_AFTER;
import static ru.practicum.shareit.TestConstant.TIME_BEFORE;
import static ru.practicum.shareit.util.Constants.USER_ID;

@WebMvcTest(controllers = BookingController.class)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingControllerTest {
    @MockBean
    private final BookingService bookingService;
    private final ObjectMapper mapper;
    private final MockMvc mvc;
    private BookingDto bookingDto;
    private NewBookingDto newBookingDto;
    private static final Long ONE_USER_ID = 1L;
    private static final Long ITEM_ID = 2L;

    @BeforeEach
    void testInitialization() {
        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(TIME_BEFORE.toString());
        bookingDto.setEnd(TIME_AFTER.toString());
        bookingDto.setStatus(BookingStatus.APPROVED);
        bookingDto.setBooker(new UserDto());
        bookingDto.setItem(new ItemDto());

        newBookingDto = new NewBookingDto();
        newBookingDto.setStart(TIME_BEFORE.toString());
        newBookingDto.setEnd(TIME_AFTER.toString());
        newBookingDto.setItemId(ITEM_ID);
    }

    @SneakyThrows
    @Test
    void create_withValidBody_thenReturnOK() {
        when(bookingService.addBooking(ONE_USER_ID, newBookingDto)).thenReturn(bookingDto);

        mvc.perform(post("/bookings")
                        .header(USER_ID, ONE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1)).addBooking(ONE_USER_ID, newBookingDto);
    }

    @SneakyThrows
    @Test
    void create_withNotExistingItemId_thenReturnNotFound() {
        newBookingDto.setItemId(NOT_EXISTING_ID);
        when(bookingService.addBooking(ONE_USER_ID, newBookingDto)).thenThrow(NotFoundException.class);

        mvc.perform(post("/bookings")
                        .header(USER_ID, ONE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(newBookingDto)))
                .andExpect(status().isNotFound());

        verify(bookingService, times(1)).addBooking(ONE_USER_ID, newBookingDto);
    }

    @SneakyThrows
    @Test
    void changeStatus_withValidParameter_thenReturnOK() {
        when(bookingService.changeStatus(ONE_USER_ID, bookingDto.getId(), true)).thenReturn(bookingDto);

        mvc.perform(patch("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, ONE_USER_ID)
                        .param("approved", String.valueOf(true))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(BookingStatus.APPROVED.name()));

        verify(bookingService, times(1)).changeStatus(ONE_USER_ID, bookingDto.getId(), true);
    }

    @SneakyThrows
    @Test
    void getAllBookings_withValidId_thenReturnOK() {
        when(bookingService.getByBooker(ONE_USER_ID, BookingSelectionState.ALL)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings")
                        .header(USER_ID, ONE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(BookingSelectionState.ALL))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1)).getByBooker(ONE_USER_ID, BookingSelectionState.ALL);
    }

    @SneakyThrows
    @Test
    void getAllBookingsByOwner_withValidId_thenReturnOK() {
        when(bookingService.getByOwner(ONE_USER_ID, BookingSelectionState.ALL)).thenReturn(List.of(bookingDto));

        mvc.perform(get("/bookings/owner")
                        .header(USER_ID, ONE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("state", String.valueOf(BookingSelectionState.ALL))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(List.of(bookingDto))));

        verify(bookingService, times(1)).getByOwner(ONE_USER_ID, BookingSelectionState.ALL);
    }

    @SneakyThrows
    @Test
    void findById_withValidId_thenReturnOK() {
        when(bookingService.getById(ONE_USER_ID, bookingDto.getId())).thenReturn(bookingDto);

        mvc.perform(get("/bookings/{bookingId}", bookingDto.getId())
                        .header(USER_ID, ONE_USER_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json(mapper.writeValueAsString(bookingDto)));

        verify(bookingService, times(1)).getById(ONE_USER_ID, bookingDto.getId());
    }
}