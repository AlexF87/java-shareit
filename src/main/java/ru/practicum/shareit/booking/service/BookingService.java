package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;

import java.util.List;

public interface BookingService {
    BookingDtoInfo create(BookingDto bookingDto, Long bookerId);
    BookingDtoInfo approve(Long bookingId, Long ownerId, Boolean approved);

    BookingDtoInfo getBookingById(Long bookingId, Long userId);

    List<BookingDtoInfo> getAllBookingsByUserId(Long userId, String state);

    List<BookingDtoInfo> getAllBookingsByOwnerId(Long userId, String state);

    Booking getByIdOrNotFoundError(Long bookingId);
}
