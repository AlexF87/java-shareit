package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.Collection;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto addItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                            @RequestBody ItemDto itemDto) {
        log.info("POST Item {}", itemDto);
        return itemService.addItem(userId, itemDto);
    }

    @GetMapping
    public List<ItemDto> getAllItems(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(name = "from", defaultValue = "0")int from,
                                     @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GetAllItems userId {}", userId);
        return itemService.getAllItems(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") long userId) {
        log.info("GetItem userId {}", userId);
        return itemService.getItem(userId, itemId);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text,
                                          @RequestParam(name = "from", defaultValue = "0")int from,
                                          @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("Search item. Text {}", text);
        return itemService.searchItem(text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto itemDto) {
        log.info("Update item {}, userId {}, itemId {}", itemDto, userId, itemId);
        return itemService.updateItem(userId, itemId, itemDto);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId,
                                    @RequestBody CommentDto commentDto) {
        commentDto.setItemId(itemId);
        log.info("POST comment {}, userId {}, itemId {} ", commentDto, userId, itemId);
        return itemService.createComment(userId, commentDto);
    }
}
