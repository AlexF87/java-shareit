package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.User;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class ItemTest {

    @Test
    void testEquals() {
        Item item = new Item();
        item.setId(1L);
        item.setName("Book");
        Item otherItem = new Item();
        otherItem.setId(2L);
        otherItem.setName("Book");

        assertFalse(item.equals(otherItem));
    }

    @Test
    void testHashCode() {
        User user = new User();
        Item item = new Item();
        item.setId(1L);
        item.setName("Book");
        item.setAvailable(true);
        item.setOwner(user);
        item.setDescription("abc");
        item.setRequestId(5L);
        Item otherItem = new Item();
        otherItem.setId(2L);
        otherItem.setName("Book");
        otherItem.setAvailable(true);
        otherItem.setOwner(user);
        otherItem.setDescription("abc");
        otherItem.setRequestId(5L);
        HashSet<Item> set = new HashSet<>();
        set.add(item);

        assertFalse(set.contains(otherItem));
    }
}