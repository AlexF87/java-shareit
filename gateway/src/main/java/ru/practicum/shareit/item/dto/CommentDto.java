package ru.practicum.shareit.item.dto;

import lombok.Getter;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
@Getter
public class CommentDto {
    private Long id;
    @NotBlank
    private String text;
    private Long itemId;
    private String authorName;
    private LocalDateTime created;
}
