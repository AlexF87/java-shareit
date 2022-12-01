package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class InMemoryItemDaoImpl implements ItemDao {

    private final Map<Long, Item> items = new HashMap<>();
    private static long idCounter = 0L;

    @Override
    public Item addItem(Long userId, Item item) {
        item.setId(++idCounter);
        items.put(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public Item getItem(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException(String.format("Item id: %d does not exist", itemId));
        }
        return items.get(itemId);
    }

    @Override
    public Collection<Item> getAllItems(Long ownerId) {
        return items.values()
                .stream()
                .filter(x -> x.getOwner().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public Item updateItem(Long itemId, Item item) {
        items.put(itemId, item);
        return item;
    }

    @Override
    public Collection<Item> search(String text) {
        return items.values()
                .stream()
                .filter(x -> x.getName().toLowerCase().contains(text.toLowerCase()) ||
                        x.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(Item::getAvailable)
                .collect(Collectors.toList());
    }
}
