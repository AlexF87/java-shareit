package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.BookingStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;

    private LocalDateTime start;
    private LocalDateTime end;
    private Long bookerId;

    private Long itemId;
    private BookingStatus state;
}
