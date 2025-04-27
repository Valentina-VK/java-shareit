package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.time.Instant;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdOrderByStart(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStart(Long bookerId, BookingStatus status);

    List<Booking> findByBookerIdAndStartLessThanEqualAndEndGreaterThan(Long bookerId, Instant now, Instant now1);

    List<Booking> findByBookerIdAndEndLessThan(Long bookerId, Instant now);

    List<Booking> findByBookerIdAndStartGreaterThan(Long bookerId, Instant now);

    List<Booking> findByItemOwnerIdOrderByStart(Long ownerId);

    List<Booking> findByItemIdOrderByStart(Long itemId);

    List<Booking> findByItemOwnerIdAndStatusOrderByStart(Long ownerId, BookingStatus status);

    List<Booking> findByItemOwnerIdAndStartLessThanEqualAndEndGreaterThan(Long ownerId, Instant now, Instant now1);

    List<Booking> findByItemOwnerIdAndEndLessThan(Long ownerId, Instant now);

    List<Booking> findByItemOwnerIdAndStartGreaterThan(Long ownerId, Instant now);

    List<Booking> findByBookerIdAndItemIdAndStatusAndEndLessThan(Long bookerId, Long itemId, BookingStatus bookingState, Instant now);
}