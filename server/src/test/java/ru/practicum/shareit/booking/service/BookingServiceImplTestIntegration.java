package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class BookingServiceImplTestIntegration {

    @Autowired
    private BookingRepository bookingRepository;

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    UserDto user;
    ItemDto item;
    BookingDto bookingDto;
    BookingDtoInfo bookingDtoInfo;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name("Tom")
                .email("tom@yahoo.vom")
                .build();
        user = userService.createUser(user);

        UserDto booker = UserDto.builder()
                .name("Jim")
                .email("jimm@yahoo.vom")
                .build();
        booker = userService.createUser(booker);

        item = ItemDto.builder()
                .name("Playstation")
                .description("5")
                .available(true)
                .owner(user.getId())
                .build();
        item = itemService.addItem(user.getId(), item);

        bookingDto = BookingDto.builder()
                .itemId(item.getId())
                .state(BookingStatus.WAITING)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingDtoInfo = bookingService.create(bookingDto, booker.getId());
    }

    @Test
    void getAllBookingsByOwnerId() {
        List<BookingDtoInfo> bookingDtoInfoList = bookingService.getAllBookingsByOwnerId(user.getId(),
                "WAITING", 0, 10);

        assertNotNull(bookingDtoInfoList);
        assertEquals(1, bookingDtoInfoList.size());
        assertEquals(bookingDto.getItemId(), bookingDtoInfoList.stream().findFirst().get().getId());
        assertEquals(bookingDto.getState(), bookingDtoInfoList.stream().findFirst().get().getStatus());
    }

    @Test
    void getBookingById() {
        BookingDtoInfo actualBooking = bookingService.getBookingById(bookingDtoInfo.getId(), user.getId());

        assertNotNull(actualBooking);
        assertEquals(bookingDtoInfo.getItem().getId(), actualBooking.getItem().getId());
        assertEquals(bookingDtoInfo.getStart(), actualBooking.getStart());
        assertEquals(bookingDtoInfo.getEnd(), actualBooking.getEnd());
    }
}