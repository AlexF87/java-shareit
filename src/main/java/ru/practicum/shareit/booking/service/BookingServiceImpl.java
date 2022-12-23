package ru.practicum.shareit.booking.service;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.handler.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.service.UserService;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
@Service
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService{
    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;
    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, @Lazy ItemService itemService,
                              UserService userService) {
        this.bookingRepository = bookingRepository;
        this.itemService = itemService;
        this.userService = userService;
    }

    @Override
    @Transactional
    public BookingDtoInfo create(BookingDto bookingDto, Long bookerId) {
        checkBooking(bookingDto, bookerId);
        Item item = itemService.getByIdOrNotFoundError(bookingDto.getItemId());
        User booker = userService.getByIdOrNotFoundError(bookerId);
        Booking booking = BookingMapper.toBooking(bookingDto, item, booker);
        booking.setStatus(BookingStatus.WAITING);
        return BookingMapper.toBookingDtoInfo(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoInfo approve(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = getByIdOrNotFoundError(bookingId);
        userService.getByIdOrNotFoundError(ownerId);
        Item item = booking.getItem();

        if (item.getOwner().getId() != ownerId) {
            throw new NotFoundException(String.format("User is not the owner. userId= %d ;  ownerId= %d",
                    ownerId, item.getOwner().getId()));
        }

        if (booking.getStatus() == BookingStatus.APPROVED || booking.getStatus() == BookingStatus.REJECTED) {
            throw new BadRequestException(String.format("Booking already %s.", booking.getStatus()));
        }

        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return  BookingMapper.toBookingDtoInfo(bookingRepository.save(booking));
    }

    @Override
    public BookingDtoInfo getBookingById(Long bookingId, Long userId) {
        userService.getByIdOrNotFoundError(userId);
        Booking booking = getByIdOrNotFoundError(bookingId);
        if (!(userId == (booking.getItem().getOwner().getId())) && !(booking.getBooker().getId() == userId)) {
            throw new NotFoundException("You are not the owner");
        }
        return BookingMapper.toBookingDtoInfo(booking);
    }

    @Override
    public List<BookingDtoInfo> getAllBookingsByUserId(Long userId, String status) {
        userService.getByIdOrNotFoundError(userId);
        BookingStatus state = checkStatus(status);
        List<Booking> bookings;
        switch(state) {
            case ALL:
                bookings = bookingRepository.findByBooker_IdOrderByEndDesc(userId);
                break;
            case CURRENT:
                bookings =
                        bookingRepository.findByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc
                                (userId, LocalDateTime.now(),LocalDateTime.now());
                break;
            case PAST:
                bookings =  bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByBooker_IdAndStartAfterOrderByEndDesc(userId,
                        LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new IllegalArgumentExceptionCustom("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDtoInfo).collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoInfo> getAllBookingsByOwnerId(Long userId, String status) {
        userService.getByIdOrNotFoundError(userId);
        BookingStatus state = checkStatus(status);
        List<Booking> bookings;
        switch (state) {
            case ALL:
                bookings = bookingRepository.findByItem_Owner_IdOrderByEndDesc(userId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc
                        (userId, LocalDateTime.now(), LocalDateTime.now());
                break;
            case PAST:
                bookings = bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(userId,
                        LocalDateTime.now());
                break;
            case FUTURE:
                bookings = bookingRepository.findByItem_Owner_IdAndStartAfterOrderByEndDesc(
                        userId, LocalDateTime.now());
                break;
            case WAITING:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusIsOrderByStartDesc(userId,
                        BookingStatus.WAITING);
                break;
            case REJECTED:
                bookings = bookingRepository.findByItem_Owner_IdAndStatusIsOrderByStartDesc(userId,
                        BookingStatus.REJECTED);
                break;
            default:
                throw new BadRequestException(String.format("Unknown status %s", state.toString()));
        }
        return bookings.stream().map(BookingMapper::toBookingDtoInfo).collect(Collectors.toList());
    }

    @Override
    public Booking getByIdOrNotFoundError(Long bookingId) {
        return bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException( String.format("Not found booking, id: %d", bookingId)));
    }

    @Override
    public List<Booking> findAllBookingsByBookerIdAndItemIdAndEndBeforeAndStatus (Long itemId,
    Long userId, LocalDateTime now, BookingStatus status, Sort sort) {
        return bookingRepository.findAllBookingsByItem_IdAndBooker_IdAndEndBeforeAndStatus(itemId,
                userId, now, status, sort);
    }

    @Override
    public List<BookingDtoForItem> getNextBooking(Long itemId, LocalDateTime now) {
        List<Booking> bookingsList = bookingRepository.findByItem_IdAndStartIsAfterOrderByStartDesc(itemId, now);
        return bookingsList
                .stream()
                .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                .map(BookingMapper::bookingDtoForItem)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDtoForItem> getLastBooking(Long itemId, LocalDateTime now) {
        return bookingRepository.findByItem_IdAndEndIsBeforeOrderByEndDesc(itemId, now)
                .stream()
                .filter(x -> x.getStatus().equals(BookingStatus.APPROVED))
                .map(BookingMapper::bookingDtoForItem)
                .collect(Collectors.toList());
    }

    private void checkBooking(BookingDto bookingDto, Long bookerId) {
        Item item = itemService.getByIdOrNotFoundError(bookingDto.getItemId());
        userService.getByIdOrNotFoundError(bookerId);
        if (!item.getAvailable()) {
            throw new BadRequestException(String.format("Not available. Item %d.", bookingDto.getItemId()));
        }
        if (item.getOwner().getId() == bookerId) {
            throw new OwnerException("Booker is the owner the item.");
        }
        LocalDateTime time = LocalDateTime.now();
        if (bookingDto.getStart().isBefore(time) || bookingDto.getEnd().isBefore(time)
                || bookingDto.getStart().isAfter(bookingDto.getEnd())) {
            throw new BadRequestException(String.format("Incorrect dates."));
        }
    }

    private BookingStatus checkStatus(String status) {
        try {
            return BookingStatus.valueOf(status);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(String.format("Unknown state: %s", status));
        }
    }
}
