package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.OwnerException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceImplTest {
    private BookingServiceImpl bookingService;
    private BookingRepository bookingRepository;
    private ItemService itemService;
    private UserService userService;

    User user;
    User owner;
    Item item;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        itemService = mock(ItemService.class);
        userService = mock(UserService.class);
        bookingService = new BookingServiceImpl(bookingRepository, itemService, userService);

        user = User.builder()
                .id(1L)
                .name("Tomas")
                .email("tomas@mail.test")
                .build();

        owner = User.builder()
                .id(2L)
                .name("Gary")
                .email("gary@mail.test")
                .build();

        item = Item.builder()
                .id(1L)
                .name("Dendy")
                .description("dendy junior")
                .owner(owner)
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .id(1L)
                .itemId(item.getId())
                .bookerId(user.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
    }

    @Test
    void create_whenAllDataValid_thenReturnBooking() {
        when(itemService.getByIdOrNotFoundError(any())).thenReturn(item);
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDto, item, user));

        BookingDtoInfo actualBooking = bookingService.create(bookingDto, user.getId());

        assertNotNull(actualBooking);
        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
        assertEquals(bookingDto.getItemId(), actualBooking.getItem().getId());
        assertEquals(bookingDto.getBookerId(), actualBooking.getBooker().getId());
    }

    @Test
    void create_whenAvailableFalse_thenThrowException() {
        item.setAvailable(false);
        when(itemService.getByIdOrNotFoundError(any())).thenReturn(item);
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);

        BadRequestException badRequestException =
                assertThrows(BadRequestException.class,
                        () -> bookingService.create(bookingDto, user.getId()));

        assertEquals(String.format("Not available. Item %d.", bookingDto.getItemId()),
                badRequestException.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void create_whenOwnerEqualsBooker_thenThrowException() {
        when(itemService.getByIdOrNotFoundError(any())).thenReturn(item);
        when(userService.getByIdOrNotFoundError(owner.getId())).thenReturn(owner);

        OwnerException ownerException =
                assertThrows(OwnerException.class,
                        () -> bookingService.create(bookingDto, owner.getId()));

        assertEquals("Booker is the owner the item.", ownerException.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenDataValidAndApproveTrue_thenReturnBookingApprove() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .item(item)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoInfo actualBooking = bookingService.approve(bookingDto.getId(), owner.getId(), true);

        assertNotNull(actualBooking);
        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
        assertEquals(bookingDto.getItemId(), actualBooking.getItem().getId());
        assertEquals(bookingDto.getBookerId(), actualBooking.getBooker().getId());
        assertEquals(BookingStatus.APPROVED, actualBooking.getStatus());
    }

    @Test
    void approve_whenDataValidAndApproveFalse_thenReturnBookingRejected() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .item(item)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);

        BookingDtoInfo actualBooking = bookingService.approve(bookingDto.getId(), owner.getId(), false);

        assertNotNull(actualBooking);
        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
        assertEquals(bookingDto.getItemId(), actualBooking.getItem().getId());
        assertEquals(bookingDto.getBookerId(), actualBooking.getBooker().getId());
        assertEquals(BookingStatus.REJECTED, actualBooking.getStatus());
    }

    @Test
    void approve_whenItemOwnerNotOwner_thenThrowException() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .item(item)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.approve(bookingDto.getId(), user.getId(), false));

        assertEquals(String.format("User is not the owner. userId= %d ;  ownerId= %d",
                user.getId(), item.getOwner().getId()), notFoundException.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenBookingStatusApproved_thenThrowException() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);

        BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> bookingService.approve(bookingDto.getId(), owner.getId(), true));

        assertEquals(String.format("Booking already %s.", booking.getStatus()), badRequestException.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenBookingStatusRejected_thenThrowException() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(bookingRepository.save(any())).thenReturn(booking);

        BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> bookingService.approve(bookingDto.getId(), owner.getId(), true));

        assertEquals(String.format("Booking already %s.", booking.getStatus()), badRequestException.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void approve_whenBookingNotFound_thenThrowException() {
        when(bookingRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.approve(bookingDto.getId(), owner.getId(), true));

        assertEquals(String.format("Not found booking, id: %d", bookingDto.getId()), notFoundException.getMessage());
        verify(bookingRepository, never()).save(any());
    }

    @Test
    void getBookingById_whenDataValid_thenReturnBooking() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingDtoInfo actualBooking = bookingService.getBookingById(booking.getId(), user.getId());

        assertNotNull(actualBooking);
        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
        assertEquals(bookingDto.getItemId(), actualBooking.getItem().getId());
        assertEquals(bookingDto.getBookerId(), actualBooking.getBooker().getId());
    }

    @Test
    void getBookingById_whenUserEqualsOwnerButBookerNotOwner_thenReturnBooking() {

        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        BookingDtoInfo actualBooking = bookingService.getBookingById(booking.getId(), owner.getId());

        assertNotNull(actualBooking);
        assertEquals(bookingDto.getStart(), actualBooking.getStart());
        assertEquals(bookingDto.getEnd(), actualBooking.getEnd());
        assertEquals(bookingDto.getItemId(), actualBooking.getItem().getId());
        assertEquals(bookingDto.getBookerId(), actualBooking.getBooker().getId());
    }

    @Test
    void getBookingById_whenUserNotBooker_thenThrowException() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findById(any())).thenReturn(Optional.of(booking));

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.getBookingById(booking.getId(), user.getId()));

        assertEquals("You are not the owner", notFoundException.getMessage());
    }

    @Test
    void getAllBookingsByUserId_whenStatusAll_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.CURRENT)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByBooker_IdOrderByEndDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByUserId(user.getId(), "ALL",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByUserId_whenStatusCURRENT_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.CURRENT)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByBooker_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(any(), any(),
                any(), any())).thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByUserId(user.getId(), "CURRENT",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByUserId_whenStatusPAST_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.PAST)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findAllByBooker_IdAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByUserId(user.getId(), "PAST",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByUserId_whenStatusFUTURE_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.FUTURE)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByBooker_IdAndStartAfterOrderByEndDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByUserId(user.getId(), "FUTURE",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByUserId_whenStatusWAITING_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByUserId(user.getId(), "WAITING",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByUserId_whenStatusREJECTED_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findAllByBooker_IdAndStatusIsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByUserId(user.getId(), "REJECTED",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByOwnerId_whenStatusAll_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.CURRENT)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByItem_Owner_IdOrderByEndDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByOwnerId(owner.getId(), "ALL",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByOwnerId_whenStatusCURRENT_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.CURRENT)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByItem_Owner_IdAndStartLessThanEqualAndEndGreaterThanOrderByEndDesc(any(), any(),
                any(), any())).thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByOwnerId(owner.getId(), "CURRENT",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByOwnerId_whenStatusPAST_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.PAST)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByItem_Owner_IdAndEndBeforeOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByOwnerId(owner.getId(), "PAST",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByOwnerId_whenStatusFUTURE_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.FUTURE)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByItem_Owner_IdAndStartAfterOrderByEndDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByOwnerId(owner.getId(), "FUTURE",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByOwnerId_whenStatusWAITING_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.WAITING)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByItem_Owner_IdAndStatusIsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByOwnerId(owner.getId(), "WAITING",
                0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void getAllBookingsByOwnerId_whenStatusREJECTED_thenReturnBookings() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(owner)
                .status(BookingStatus.REJECTED)
                .item(item)
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(bookingRepository.findByItem_Owner_IdAndStatusIsOrderByStartDesc(any(), any(), any()))
                .thenReturn(List.of(booking));

        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByOwnerId(owner.getId(),
                "REJECTED", 0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
    }

    @Test
    void findAllBookingsByBookerIdAndItemIdAndEndBeforeAndStatus() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.findAllBookingsByItem_IdAndBooker_IdAndEndBeforeAndStatus(any(), any(), any(), any(),
                any())).thenReturn(List.of(booking));

        List<Booking> bookingList =
                bookingService.findAllBookingsByBookerIdAndItemIdAndEndBeforeAndStatus(item.getId(), user.getId(),
                        LocalDateTime.now(), BookingStatus.APPROVED, Sort.unsorted());

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }

    @Test
    void getNextBooking() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.findByItem_IdAndStartIsAfterOrderByStartDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoForItem> bookingList = bookingService.getNextBooking(item.getId(), LocalDateTime.now());

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }

    @Test
    void getLastBooking() {
        Booking booking = Booking.builder()
                .id(bookingDto.getItemId())
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(user)
                .status(BookingStatus.APPROVED)
                .item(item)
                .build();
        when(bookingRepository.findByItem_IdAndEndIsBeforeOrderByEndDesc(any(), any())).thenReturn(List.of(booking));

        List<BookingDtoForItem> bookingList = bookingService.getLastBooking(item.getId(), LocalDateTime.now());

        assertNotNull(bookingList);
        assertEquals(1, bookingList.size());
    }
}