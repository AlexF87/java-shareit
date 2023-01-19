package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.Getter;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.util.List;

@Getter
@Data
public class ItemDto {
    private long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotNull
    private Boolean available;
    @Positive
    private Long requestId;

    private Long owner;
    private Long request;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<CommentDto> comments;
}
