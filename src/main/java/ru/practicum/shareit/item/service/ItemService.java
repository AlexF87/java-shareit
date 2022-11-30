package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Collection;

public interface ItemService {
    ItemDto addItem(Long userId, ItemDto itemDto);

    ItemDto getItem(Long itemId);

    Collection<ItemDto>  getAllItems(Long userId);

    ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto);

    Collection<ItemDto> searchItem(String text);
}
