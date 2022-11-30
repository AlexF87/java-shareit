package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.OwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemDao itemDao;
    private final UserService userService;

    @Override
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        Item item = ItemMapper.toItem(itemDto, userId);
        checkItemValid(itemDto);
        userService.getUser(userId);
        return ItemMapper.toItemDto(itemDao.addItem(userId, item));
    }

    @Override
    public ItemDto getItem(Long itemId) {
        return ItemMapper.toItemDto(itemDao.getItem(itemId));
    }

    @Override
    public Collection<ItemDto> getAllItems(Long userId) {
        return itemDao.getAllItems(useId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = itemDao.getItem(itemId);

        if (item == null) {
            throw new NotFoundException(String.format("No item with id: %d  +", itemId));
        }
        if (!itemDao.getItem(itemId).getOwner.equals(userId)) {
            throw new OwnerException(String.format("This user don't owner"));
        }
        if (itemDto.getName() != null) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemDao.updateItem(itemid, item));
    }

    @Override
    public Collection<ItemDto> searchItem(String text) {
        return itemDao.search(text);
    }

    private void checkItemValid(ItemDto itemDto) {
        if (itemDto.getName().isEmpty() || itemDto.getName() == null || itemDto.getDescription().isEmpty()
        || itemDto.getDescription() == null || itemDto.getAvailable() == null) {
            throw new ValidationException(String.format("Validation error itemDto name: %s description: %s available:" +
                    " " +
                    "%b", itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable()));
        }
    }
}
