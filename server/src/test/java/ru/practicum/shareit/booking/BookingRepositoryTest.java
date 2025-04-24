package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.Instant;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.TestConstant.TIME_AFTER;
import static ru.practicum.shareit.TestConstant.TIME_BEFORE;

@Transactional
@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private User owner;
    private Item item1;
    private User booker;
    private Booking booking1;
    private Item item2;
    private Booking booking2;

    @BeforeEach
    void initBase() {
        owner = new User();
        owner.setName("TestNameOwner");
        owner.setEmail("testOwner@yandex.ru");
        owner = userRepository.save(owner);

        item1 = new Item();
        item1.setOwnerId(owner.getId());
        item1.setName("TestItem1");
        item1.setDescription("for test 1");
        item1.setAvailable(true);
        item1 = itemRepository.save(item1);

        booker = new User();
        booker.setName("TestNameBooker1");
        booker.setEmail("testBooker1@yandex.ru");
        booker = userRepository.save(booker);

        booking1 = new Booking();
        booking1.setStart(TIME_BEFORE);
        booking1.setEnd(TIME_AFTER);
        booking1.setBooker(booker);
        booking1.setItem(item1);
        booking1 = bookingRepository.save(booking1);

        item2 = new Item();
        item2.setOwnerId(owner.getId());
        item2.setName("TestItem2");
        item2.setDescription("for test 2");
        item2.setAvailable(true);
        item2 = itemRepository.save(item2);

        booking2 = new Booking();
        booking2.setStart(TIME_AFTER);
        booking2.setEnd(TIME_AFTER.plusMillis(10000000L));
        booking2.setBooker(booker);
        booking2.setItem(item2);
        booking2 = bookingRepository.save(booking2);
    }


    @Test
    void findByBookerIdOrderByStart_whenItExisting_thenReturnResult() {

        List<Booking> result = bookingRepository.findByBookerIdOrderByStart(booker.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst(), equalTo(booking1));
        assertThat(result.getFirst().getItem(), equalTo(item1));
        assertThat(result.getFirst().getBooker(), equalTo(booker));
    }

    @Test
    void findByBookerIdAndStatusOrderByStart_whenItExisting_thenReturnResult() {
        booking1.setStatus(BookingStatus.APPROVED);

        List<Booking> result = bookingRepository.findByBookerIdAndStatusOrderByStart(booker.getId(),
                BookingStatus.APPROVED);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking1));
        assertThat(result.getFirst().getItem(), equalTo(item1));
        assertThat(result.getFirst().getBooker(), equalTo(booker));
    }

    @Test
    void findByBookerIdAndStartLessThanEqualAndEndGreaterThan_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByBookerIdAndStartLessThanEqualAndEndGreaterThan(
                booker.getId(), Instant.now(), Instant.now());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking1));
        assertThat(result.getFirst().getItem(), equalTo(item1));
        assertThat(result.getFirst().getBooker(), equalTo(booker));
    }

    @Test
    void findByBookerIdAndEndLessThan_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByBookerIdAndEndLessThan(booker.getId(), Instant.now());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findByBookerIdAndStartGreaterThan_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByBookerIdAndStartGreaterThan(
                booker.getId(), Instant.now());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking2));
        assertThat(result.getFirst().getItem(), equalTo(item2));
        assertThat(result.getFirst().getBooker(), equalTo(booker));
    }

    @Test
    void findByItemOwnerIdOrderByStart_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStart(
                owner.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(2));
        assertThat(result.getFirst(), equalTo(booking1));
        assertThat(result.getFirst().getItem(), equalTo(item1));
    }

    @Test
    void findByItemOwnerIdOrderByStart_whenItNotExisting_thenReturnEmptyList() {
        List<Booking> result = bookingRepository.findByItemOwnerIdOrderByStart(
                booker.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findByItemIdOrderByStart_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByItemIdOrderByStart(item2.getId());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking2));
        assertThat(result.getFirst().getItem(), equalTo(item2));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStart_whenItExisting_thenReturnResult() {
        booking1.setStatus(BookingStatus.APPROVED);

        List<Booking> result = bookingRepository.findByItemOwnerIdAndStatusOrderByStart(owner.getId(),
                BookingStatus.APPROVED);

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking1));
        assertThat(result.getFirst().getItem(), equalTo(item1));
    }

    @Test
    void findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThan_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThan(
                owner.getId(), Instant.now(), Instant.now());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking1));
        assertThat(result.getFirst().getItem(), equalTo(item1));
    }

    @Test
    void findByItemOwnerIdAndEndLessThan_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByItemOwnerIdAndEndLessThan(owner.getId(), Instant.now());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(0));
    }

    @Test
    void findByItemOwnerIdAndStartGreaterThan_whenItExisting_thenReturnResult() {
        List<Booking> result = bookingRepository.findByItemOwnerIdAndStartGreaterThan(
                owner.getId(), Instant.now());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking2));
        assertThat(result.getFirst().getItem(), equalTo(item2));
    }

    @Test
    void findByBookerIdAndItemIdAndStatusAndEndLessThan_whenItExisting_thenReturnResult() {
        booking1.setStatus(BookingStatus.APPROVED);

        List<Booking> result = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndLessThan(booker.getId(),
                item1.getId(), BookingStatus.APPROVED, booking2.getEnd());

        assertThat(result, notNullValue());
        assertThat(result.size(), equalTo(1));
        assertThat(result.getFirst(), equalTo(booking1));
        assertThat(result.getFirst().getItem(), equalTo(item1));
    }
}