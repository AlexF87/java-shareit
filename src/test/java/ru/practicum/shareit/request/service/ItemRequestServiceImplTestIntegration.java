package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemRequestServiceImplTestIntegration {
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ItemRequestService itemRequestService;

    UserDto userDto;
    ItemRequestDto itemRequest;

    @BeforeEach
    void setUp() {
        userDto = UserDto.builder()
                .name("Bill")
                .email("karash@mail.rt")
                .build();
        userDto = userService.createUser(userDto);
        itemRequest = ItemRequestDto.builder()
                .created(LocalDateTime.now())
                .description("book")
                .build();
        itemRequest = itemRequestService.createRequest(itemRequest, userDto.getId());
    }

    @Test
    void getRequestById() {
        ItemRequestDto actualItemRequest = itemRequestService.getRequestById(itemRequest.getId(), userDto.getId());

        assertNotNull(actualItemRequest);
        assertEquals(itemRequest.getId(), actualItemRequest.getId());
        assertEquals(itemRequest.getDescription(), actualItemRequest.getDescription());
        assertEquals(itemRequest.getCreated(), actualItemRequest.getCreated());
    }

    @AfterEach()
    void deleteAll() {
        itemRequestRepository.deleteAll();
    }


}