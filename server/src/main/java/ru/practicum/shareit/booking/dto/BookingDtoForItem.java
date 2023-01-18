package ru.practicum.shareit.booking.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookingDtoForItem {
    private long id;
    private long bookerId;

    public BookingDtoForItem(Long id, Long bookerId) {
        this.id = id;
        this.bookerId = bookerId;
    }
}
