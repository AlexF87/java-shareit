package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.List;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItem(Long userId, Long itemId);

    List<ItemDto> getAllItems(Long userId, int from ,int size);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> searchItem(String text, int from, int size);

    Item getByIdOrNotFoundError(Long itemId);

    CommentDto createComment(Long userId, CommentDto commentDto);
}
