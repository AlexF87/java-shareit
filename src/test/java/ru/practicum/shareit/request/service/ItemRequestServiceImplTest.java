package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemRequestServiceImplTest {
    private ItemRequestRepository itemRequestRepository;
    private UserService userService;
    private ItemRequestServiceImpl itemRequestService;

    User user;

    @BeforeEach
    void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        userService = mock(UserService.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService);

        user = User.builder()
                .id(10L)
                .name("Bob")
                .email("bob@mail.test")
                .build();
    }

    @Test
    void createRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .requestId(1L)
                .description("Book")
                .creationTime(LocalDateTime.now().withNano(0))
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(itemRequestRepository.save(any())).thenReturn(itemRequest);

        ItemRequestDto actualItemRequestDto =
                itemRequestService.createRequest(ItemRequestMapper.toItemRequestDto(itemRequest), user.getId());

        assertNotNull(actualItemRequestDto);
        assertEquals(itemRequest.getDescription(), actualItemRequestDto.getDescription());
        assertEquals(itemRequest.getRequestId(), actualItemRequestDto.getId());
        assertEquals(itemRequest.getCreationTime(), actualItemRequestDto.getCreated());
    }

    @Test
    void getItemRequestsByUserId() {
        ItemRequest itemRequest = ItemRequest.builder()
                .requestId(1L)
                .description("Book")
                .creationTime(LocalDateTime.now().withNano(0))
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(itemRequestRepository.findAllByUserIdOrderByCreationTimeDesc(any())).thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getItemRequestsByUserId(user.getId());

        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
        verify(itemRequestRepository, times(1))
                .findAllByUserIdOrderByCreationTimeDesc(user.getId());
    }

    @Test
    void getAllRequests() {
        ItemRequest itemRequest = ItemRequest.builder()
                .requestId(1L)
                .description("Book")
                .creationTime(LocalDateTime.now().withNano(0))
                .build();
        when(itemRequestRepository.findAllByUserIdIsNotOrderByCreationTimeDesc(any(), any()))
                .thenReturn(List.of(itemRequest));

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllRequests(user.getId(),0, 10);

        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
        verify(itemRequestRepository, times(1))
                .findAllByUserIdIsNotOrderByCreationTimeDesc(user.getId(), CustomPageRequest.of(0, 10));
    }

    @Test
    void getRequestById_whenItemRequestExists_thenReturnItemRequest() {
        ItemRequest itemRequest = ItemRequest.builder()
                .requestId(1L)
                .description("Book")
                .creationTime(LocalDateTime.now().withNano(0))
                .build();
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.of(itemRequest));

        ItemRequestDto actualItemrequest = itemRequestService.getRequestById(itemRequest.getRequestId(), user.getId());

        assertNotNull(actualItemrequest);
        assertEquals(itemRequest.getRequestId(), actualItemrequest.getId());
        assertEquals(itemRequest.getDescription(), actualItemrequest.getDescription());
        assertEquals(itemRequest.getCreationTime(), actualItemrequest.getCreated());
    }

    @Test
    void getRequestById_whenItemRequestNotExists_thenThrowException() {
        Long requestId = 1L;
        when(userService.getByIdOrNotFoundError(user.getId())).thenReturn(user);
        when(itemRequestRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemRequestService.getRequestById(requestId, user.getId()));

        assertEquals(String.format("Not exists itemRequest %d ",requestId), notFoundException.getMessage());
    }
}