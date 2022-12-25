package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    BookingDtoInfo create(BookingDto bookingDto, Long bookerId);

    BookingDtoInfo approve(Long bookingId, Long ownerId, Boolean approved);

    BookingDtoInfo getBookingById(Long bookingId, Long userId);

    List<BookingDtoInfo> getAllBookingsByUserId(Long userId, String state);

    List<BookingDtoInfo> getAllBookingsByOwnerId(Long userId, String state);

    Booking getByIdOrNotFoundError(Long bookingId);

    List<Booking> findAllBookingsByBookerIdAndItemIdAndEndBeforeAndStatus(Long itemId, Long userId,
                                                                          LocalDateTime now,
                                                                          BookingStatus status,
                                                                          Sort sort);

    List<BookingDtoForItem> getNextBooking(Long itemId, LocalDateTime now);

    List<BookingDtoForItem> getLastBooking(Long itemId, LocalDateTime now);
}
