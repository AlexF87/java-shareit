package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @Autowired
    public ItemRequestController(ItemRequestService itemRequestService) {
        this.itemRequestService = itemRequestService;
    }
    @PostMapping
    public ItemRequestDto createRequest(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @Valid @RequestBody ItemRequestDto itemRequestDto) {
        log.info("POST createRequest : {}", itemRequestDto);
        return itemRequestService.createRequest(itemRequestDto, userId);

    }

    @GetMapping
    public List<ItemRequestDto> getItemRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        log.info("GET request by UserId : {}", userId);
        return itemRequestService.getItemRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(
                                             @RequestHeader("X-Sharer-User-Id") Long userId,
                                             @PositiveOrZero @RequestParam(name = "from", defaultValue = "0")int from,
                                             @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.info("GET getAllRequest");
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long requestId) {
        log.info("GET getRequestId : {}", requestId);
        return itemRequestService.getRequestById(requestId, userId);
    }
}
