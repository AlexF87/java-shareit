package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.OwnerException;
import ru.practicum.shareit.handler.exception.ValidationException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public ItemDto addItem(Long userId, ItemDto itemDto) {
        User user = userService.getByIdOrNotFoundError(userId);
        Item item = ItemMapper.toItem(itemDto, user);
        checkItemValid(itemDto);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto getItem(Long userId, Long itemId) {
        User user = userService.getByIdOrNotFoundError(userId);
        Item item = getByIdOrNotFoundError(itemId);
        ItemDto dto = ItemMapper.toItemDto(item);
        if (userId.longValue() == item.getOwner().getId().longValue()) {
            BookingDtoForItem next = bookingService.getNextBooking(item.getId(), LocalDateTime.now())
                    .stream()
                    .findFirst()
                    .orElse(null);
            BookingDtoForItem last = bookingService.getLastBooking(item.getId(), LocalDateTime.now())
                    .stream()
                    .findFirst()
                    .orElse(null);
            dto.setNextBooking(next);
            dto.setLastBooking(last);
        }
        return dto;
    }

    @Override
    public List<ItemDto> getAllItems(Long userId, int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        userService.getByIdOrNotFoundError(userId);
        List<ItemDto> itemDtoList = itemRepository.findByOwner_Id(userId, pageable)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        itemDtoList.stream().forEach(itemDto -> {
            BookingDtoForItem next = bookingService.getNextBooking(itemDto.getId(), LocalDateTime.now())
                    .stream()
                    .findFirst()
                    .orElse(null);
            BookingDtoForItem last =
                    bookingService.getLastBooking(itemDto.getId(), LocalDateTime.now())
                            .stream()
                            .findFirst()
                            .orElse(null);
            itemDto.setNextBooking(next);
            itemDto.setLastBooking(last);
        });
        return itemDtoList;
    }

    @Override
    @Transactional
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        Item item = getByIdOrNotFoundError(itemId);
        if (item.getOwner().getId().longValue() != userId.longValue()) {
            throw new OwnerException(String.format("This user don't owner"));
        }
        if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            item.setAvailable(itemDto.getAvailable());
        }
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public Collection<ItemDto> searchItem(String text, int from, int size) {
        Pageable pageable = CustomPageRequest.of(from, size);
        if (text.isEmpty()) {
            return new ArrayList<>();
        }
        return itemRepository
                .findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(text, text, pageable)
                .stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    private void checkItemValid(ItemDto itemDto) {
        if (itemDto.getName() == null || itemDto.getName().isEmpty() || itemDto.getDescription() == null ||
                itemDto.getDescription().isEmpty() || itemDto.getAvailable() == null) {
            throw new ValidationException(String.format("Validation error itemDto name: %s description: %s available:" +
                    " %b", itemDto.getName(), itemDto.getDescription(), itemDto.getAvailable()));
        }
    }

    @Override
    public Item getByIdOrNotFoundError(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Not found item %d", itemId)));
    }

    @Override
    public CommentDto createComment(Long userId, CommentDto commentDto) {
        User user = userService.getByIdOrNotFoundError(userId);
        Item item = getByIdOrNotFoundError(commentDto.getItemId());
        List<Booking> booking = bookingService
                .findAllBookingsByBookerIdAndItemIdAndEndBeforeAndStatus(item.getId(), userId, LocalDateTime.now(),
                        BookingStatus.APPROVED, Sort.by("start").descending());
        if (booking == null || booking.size() == 0) {
            throw new BadRequestException("Comments are left after the end of the lease");
        }
        Comment comment = CommentMapper.toComment(commentDto, item, user);
        return CommentMapper.toCommentDto(commentRepository.save(comment));
    }
}
