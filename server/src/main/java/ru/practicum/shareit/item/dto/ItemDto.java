package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import lombok.ToString;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;


@Getter
@Setter
@Builder
@ToString
public class ItemDto {
    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long owner;
    private Long request;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<CommentDto> comments;
    private Long requestId;
}
