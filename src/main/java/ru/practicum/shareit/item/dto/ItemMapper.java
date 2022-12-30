package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ItemMapper {
    public static ItemDto toItemDto(Item item) {
        if (item.getComments() == null) {
            item.setComments(new ArrayList<>());
        }
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(item.getOwner().getId())
                .comments(item.getComments()
                        .stream()
                        .map(CommentMapper::toCommentDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static Item toItem(ItemDto itemDto, User owner) {

        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available((itemDto.getAvailable()))
                .owner(owner)
                .build();
    }
}
