package ru.practicum.shareit.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exceptions.NoAccessException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemBookingDatesDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validateService.ValidateService;

import java.time.Duration;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.TestConstant.NOT_EXISTING_ID;
import static ru.practicum.shareit.TestConstant.NOT_OWNER_ID;
import static ru.practicum.shareit.TestConstant.TIME_AFTER;
import static ru.practicum.shareit.TestConstant.TIME_BEFORE;


@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private ValidateService validateService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemMapper mapper;
    @InjectMocks
    private ItemServiceImpl itemService;
    private Item item;
    private ItemDto itemDto;

    @BeforeEach
    void testInitialization() {
        item = new Item();
        item.setId(1L);
        item.setOwnerId(10L);
        item.setName("TestItem");
        item.setDescription("for test");
        item.setAvailable(true);
        item.setComments(List.of("test comment"));

        itemDto = new ItemDto();
        itemDto.setId(item.getId());
        itemDto.setName(item.getName());
        itemDto.setDescription(item.getDescription());
        itemDto.setAvailable(item.getAvailable());
        itemDto.setComments(item.getComments());
    }

    @Test
    void getAll_withValidUserId_thenReturnRightResult() {
        when(itemRepository.findByOwnerId(item.getOwnerId())).thenReturn(List.of(item));
        when(mapper.toDto(List.of(item))).thenReturn(List.of(itemDto));

        List<ItemDto> result = itemService.getAll(item.getOwnerId());

        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(itemDto));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(itemRepository, times(1)).findByOwnerId(item.getOwnerId());
        verify(mapper, times(1)).toDto(List.of(item));
    }

    @Test
    void getAll_withNotExistingUserId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.getAll(NOT_EXISTING_ID));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void get_withUserIdIsOwnerIdAndItemExist_thenReturnItemWithDatesOfBooking() {
        ItemBookingDatesDto itemWithDates = new ItemBookingDatesDto();
        itemWithDates.setId(item.getId());
        itemWithDates.setLastBooking(TIME_BEFORE.toString());
        itemWithDates.setNextBooking(TIME_AFTER.toString());
        when(validateService.checkItem(item.getId())).thenReturn(item);
        when(bookingRepository.findByItemIdOrderByStart(item.getId())).thenReturn(getInitBookings());
        when(mapper.toBookingDatesDto(item, TIME_BEFORE, TIME_AFTER)).thenReturn(itemWithDates);

        ItemBookingDatesDto result = itemService.get(item.getOwnerId(), item.getId());
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getLastBooking(), equalTo(itemWithDates.getLastBooking()));
        assertThat(result.getNextBooking(), equalTo(itemWithDates.getNextBooking()));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(validateService, times(1)).checkItem(item.getId());
        verify(bookingRepository, times(1)).findByItemIdOrderByStart(item.getId());
        verify(mapper, times(1)).toBookingDatesDto(item, TIME_BEFORE, TIME_AFTER);
    }

    @Test
    void get_withUserIdIsNotOwnerIdAndItemExist_thenReturnItemWithDatesOfBookingNull() {
        ItemBookingDatesDto itemWithDates = new ItemBookingDatesDto();
        itemWithDates.setId(item.getId());
        itemWithDates.setLastBooking(null);
        itemWithDates.setNextBooking(null);
        when(validateService.checkItem(item.getId())).thenReturn(item);
        when(mapper.toBookingDatesDto(item, null, null)).thenReturn(itemWithDates);

        ItemBookingDatesDto result = itemService.get(NOT_OWNER_ID, item.getId());
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));
        assertThat(result.getLastBooking(), equalTo(itemWithDates.getLastBooking()));
        assertThat(result.getNextBooking(), equalTo(itemWithDates.getNextBooking()));

        verify(validateService, times(1)).checkUser(NOT_OWNER_ID);
        verify(validateService, times(1)).checkItem(item.getId());
        verify(bookingRepository, never()).findByItemIdOrderByStart(any());
        verify(mapper, times(1)).toBookingDatesDto(item, null, null);
    }

    @Test
    void get_withNotExistingItemId_thenThrowNotFoundException() {
        when(validateService.checkItem(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.get(item.getOwnerId(), NOT_EXISTING_ID));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(validateService, times(1)).checkItem(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void get_withNotExistingUserId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.get(NOT_EXISTING_ID, item.getId()));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, bookingRepository, mapper);
    }

    @Test
    void search_withValidUserIdAndSuitableText_thenReturnRightResult() {
        when(itemRepository.findByNameContainingIgnoreCaseAndAvailableTrue("TEST"))
                .thenReturn(List.of(item));
        when(mapper.toDto(List.of(item))).thenReturn(List.of(itemDto));

        List<ItemDto> result = itemService.search(item.getOwnerId(), "TEST");

        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(itemDto));
        assertThat(result.getFirst().getName(), equalTo(item.getName()));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(itemRepository, times(1)).findByNameContainingIgnoreCaseAndAvailableTrue("TEST");
        verify(mapper, times(1)).toDto(List.of(item));
    }

    @Test
    void search_withValidUserIdAndTextIsBlank_thenReturnEmptyList() {
        List<ItemDto> result = itemService.search(item.getOwnerId(), "");

        assertTrue(result.isEmpty());

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void search_withNotExistingUserId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.search(NOT_EXISTING_ID, "TEXT"));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void save_withValidUserIdAndItemDto_thenReturnCreatedItem() {
        Item newItem = new Item();
        newItem.setName(itemDto.getName());
        newItem.setDescription(itemDto.getDescription());
        when(mapper.toEntity(itemDto)).thenReturn(newItem);
        when(itemRepository.save(newItem)).thenReturn(item);
        when(mapper.toDto(item)).thenReturn(itemDto);

        ItemDto result = itemService.save(item.getOwnerId(), itemDto);
        assertThat(result, notNullValue());
        assertThat(result.getId(), equalTo(item.getId()));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(itemRepository, times(1)).save(newItem);
        verify(mapper, times(1)).toEntity(itemDto);
        verify(mapper, times(1)).toDto(item);
    }

    @Test
    void save_withValidUserIdAndItemDtoIsNull_thenThrowRuntimeException() {
        when(mapper.toEntity(null)).thenReturn(null);

        assertThrows((RuntimeException.class), () -> itemService.save(item.getOwnerId(), null));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(mapper, times(1)).toEntity(null);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void save_withVNotExistingUserId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.save(NOT_EXISTING_ID, itemDto));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void update_withUserIdIsOwnerIdAndExistingItemId_thenReturnUpdatedItem() {
        when(validateService.checkItem(item.getId())).thenReturn(item);
        when(mapper.toDto(item)).thenReturn(itemDto);
        when(itemRepository.save(item)).thenReturn(item);

        ItemDto result = itemService.update(item.getOwnerId(), item.getId(), itemDto);

        assertThat(result, notNullValue());
        assertThat(result, equalTo(itemDto));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(validateService, times(1)).checkItem(item.getId());
        verify(itemRepository, times(1)).save(item);
        verify(mapper, times(1)).update(itemDto, item);
        verify(mapper, times(1)).toDto(item);
    }

    @Test
    void update_withUserIdIsNotOwnerIdAndExistingItemId_thenThrowNoAccessException() {
        when(validateService.checkItem(item.getId())).thenReturn(item);

        assertThrows((NoAccessException.class), () -> itemService.update(NOT_OWNER_ID, item.getId(), itemDto));

        verify(validateService, times(1)).checkUser(NOT_OWNER_ID);
        verify(validateService, times(1)).checkItem(item.getId());
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void update_withNotExistingUserId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.update(NOT_EXISTING_ID, item.getId(), itemDto));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void update_withNotExistingItemId_thenThrowNotFoundException() {
        when(validateService.checkItem(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.update(item.getOwnerId(), NOT_EXISTING_ID, itemDto));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(validateService, times(1)).checkItem(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);

    }

    @Test
    void delete_withUserIdIsOwnerIdAndExistingItemId_thenEndCorrectly() {
        when(validateService.checkItem(item.getId())).thenReturn(item);

        assertDoesNotThrow(() -> itemService.delete(item.getOwnerId(), item.getId()));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(validateService, times(1)).checkItem(item.getId());
        verify(itemRepository, times(1)).deleteById(item.getId());
    }

    @Test
    void delete_withUserIdIsNotOwnerIdAndExistingItemId_thenThrowNoAccessException() {
        when(validateService.checkItem(item.getId())).thenReturn(item);

        assertThrows((NoAccessException.class), () -> itemService.delete(NOT_OWNER_ID, item.getId()));

        verify(validateService, times(1)).checkUser(NOT_OWNER_ID);
        verify(validateService, times(1)).checkItem(item.getId());
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void delete_withNotExistingUserId_thenThrowNotFoundException() {
        when(validateService.checkUser(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.delete(NOT_EXISTING_ID, item.getId()));

        verify(validateService, times(1)).checkUser(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    @Test
    void delete_withNotExistingItemId_thenThrowNotFoundException() {

        when(validateService.checkItem(NOT_EXISTING_ID)).thenThrow(NotFoundException.class);

        assertThrows((NotFoundException.class), () -> itemService.delete(item.getOwnerId(), NOT_EXISTING_ID));

        verify(validateService, times(1)).checkUser(item.getOwnerId());
        verify(validateService, times(1)).checkItem(NOT_EXISTING_ID);
        verifyNoMoreInteractions(validateService, itemRepository, mapper);
    }

    private List<Booking> getInitBookings() {
        Booking lastBooking = new Booking();
        lastBooking.setId(5L);
        lastBooking.setStart(TIME_BEFORE);
        lastBooking.setEnd(TIME_BEFORE.plus(Duration.ofDays(1)));
        lastBooking.setStatus(BookingStatus.APPROVED);
        lastBooking.setItem(item);
        lastBooking.setBooker(new User());

        Booking nextBooking = new Booking();
        nextBooking.setId(15L);
        nextBooking.setStart(TIME_AFTER);
        nextBooking.setEnd(TIME_AFTER.plus(Duration.ofDays(1)));
        nextBooking.setStatus(BookingStatus.APPROVED);
        nextBooking.setItem(item);
        nextBooking.setBooker(new User());
        return List.of(lastBooking, nextBooking);
    }

}