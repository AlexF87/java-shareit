package ru.practicum.shareit.request.dto;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ItemRequestMapper {
    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        List<ItemDto> itemDtoList;

        if (itemRequest.getItems() == null) {
            itemRequest.setItems(new ArrayList<>());
        }
        itemDtoList = itemRequest.getItems()
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return ItemRequestDto.builder()
                .items(itemDtoList)
                .id(itemRequest.getRequestId())
                .created(itemRequest.getCreationTime())
                .description(itemRequest.getDescription())
                .build();
    }

    public static ItemRequest toItemRequest(ItemRequestDto itemRequestDto, User user) {
        return  ItemRequest.builder()
                .creationTime(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .userId(user.getId())
                .build();
    }
}
