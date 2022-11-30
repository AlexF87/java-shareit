package ru.practicum.shareit.item;

import ru.practicum.shareit.item.model.Item;

import java.util.Collection;

public interface ItemDao {
    Item addItem(Long userId, Item item);

    Item getItem(Long itemId);

    Collection<Item> getAllItems(Long ownerId);

    Item updateItem(Long itemId, Item item);

    Collection<Item> search(String text);
}
