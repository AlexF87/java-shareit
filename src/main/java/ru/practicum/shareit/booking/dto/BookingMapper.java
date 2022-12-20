package ru.practicum.shareit.booking.dto;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.service.UserService;

@Component
public class BookingMapper {
    private final ItemService itemService;
    private final UserService userService;

    @Autowired
    public BookingMapper(ItemService itemService, UserService userService) {
        this.itemService = itemService;
        this.userService = userService;
    }

    public Booking toBooking(BookingDto bookingDto, Long bookerId) {
        Item item = itemService.getByIdOrNotFoundError(bookingDto.getItemId());
        User booker = userService.getByIdOrNotFoundError(bookerId);
        return Booking.builder()
                .start(bookingDto.getStart())
                .end(bookingDto.getEnd())
                .booker(booker)
                .item(item)
                .build();
    }


    public static BookingDtoInfo toBookingDtoInfo(Booking booking) {
        return BookingDtoInfo.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end( booking.getEnd())
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .item(ItemMapper.toItemDto(booking.getItem()))
                .status(booking.getStatus())
                .build();
    }
}
