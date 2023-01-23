package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.handler.exception.OwnerException;
import ru.practicum.shareit.item.CommentRepository;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ItemServiceImplTest {
    private ItemServiceImpl itemService;
    private UserService userService;
    private BookingService bookingService;
    private ItemRepository itemRepository;
    private CommentRepository commentRepository;
    User user;
    ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userService = mock(UserService.class);
        bookingService = mock(BookingService.class);
        itemRepository = mock(ItemRepository.class);
        commentRepository = mock(CommentRepository.class);
        itemService = new ItemServiceImpl(userService, bookingService, itemRepository, commentRepository);

        itemDto = ItemDto.builder()
                .id(1L)
                .name("Book and disk")
                .description("Java")
                .available(true)
                .comments(new ArrayList<>())
                .build();

        user = User.builder()
                .id(1L)
                .email("test@mail.test")
                .name("Bill")
                .build();
    }

    @Test
    void addItem_whenCreateItem_thenReturnItem() {
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto, user));

        ItemDto actualItem = itemService.addItem(user.getId(), itemDto);

        assertNotNull(actualItem);
        assertEquals(itemDto.getName(), actualItem.getName());
        assertEquals(itemDto.getDescription(), actualItem.getDescription());
        assertEquals(itemDto.getId(), actualItem.getId());
        verify(itemRepository).save(ItemMapper.toItem(itemDto, user));
    }

    @Test
    void getItem_whenFoundItem_thenReturnItem() {
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.of(ItemMapper.toItem(itemDto, user)));

        ItemDto actualItem = itemService.getItem(user.getId(), itemDto.getId());

        assertNotNull(actualItem);
        assertEquals(itemDto.getName(), actualItem.getName());
        assertEquals(itemDto.getDescription(), actualItem.getDescription());
        assertEquals(itemDto.getId(), actualItem.getId());
        verify(itemRepository).findById(itemDto.getId());
    }

    @Test
    void getItem_whenNotFoundItem_thenReturnNotFoundException() {
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(itemRepository.findById(any())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> itemService.getItem(user.getId(), itemDto.getId()));
        assertEquals(String.format("Not found item %d", itemDto.getId()), notFoundException.getMessage());

        verify(itemRepository).findById(itemDto.getId());
    }

    @Test
    void getAllItems_whenGetItems_thenReturnListItems() {
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(itemRepository.findByOwner_IdOrderByIdAsc(user.getId(), CustomPageRequest.of(1, 1)))
                .thenReturn(List.of(ItemMapper.toItem(itemDto, user)));

        List<ItemDto> listItemDto = itemService.getAllItems(user.getId(), 1, 1);

        assertEquals(1, listItemDto.size());
        verify(itemRepository).findByOwner_IdOrderByIdAsc(user.getId(), CustomPageRequest.of(1, 1));
    }

    @Test
    void updateItem_whenItemUpdate_thenUpdateFields() {
        when(itemRepository.findById(any())).thenReturn(Optional.of(ItemMapper.toItem(itemDto, user)));
        when(itemRepository.save(any())).thenReturn(ItemMapper.toItem(itemDto, user));

        ItemDto actualItem = itemService.updateItem(user.getId(), itemDto.getId(), itemDto);

        assertEquals("Book and disk", actualItem.getName());
        assertEquals(itemDto.getId(), actualItem.getId());
        assertEquals("Java", actualItem.getDescription());
        assertTrue(actualItem.getAvailable());
    }

    @Test
    void updateItem_whenUserIsNotOwner_thenReturnException() {
        when(itemRepository.findById(any())).thenReturn(Optional.of(ItemMapper.toItem(itemDto, user)));


        OwnerException ownerException = assertThrows(OwnerException.class,
                () -> itemService.updateItem(2L, itemDto.getId(), itemDto));

        assertEquals(String.format("This user don't owner"), ownerException.getMessage());
        verify(itemRepository, never()).save(ItemMapper.toItem(itemDto, user));
    }

    @Test
    void searchItem_whenTextEmpty_thenReturnItem() {
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(anyString(),
                anyString(), any())).thenReturn(List.of());

        Collection<ItemDto> items = itemService.searchItem("", 1, 1);

        assertEquals(0, items.size());
    }

    @Test
    void searchItem_whenTextNotEmpty_thenReturnItem() {
        itemDto.setOwner(user.getId());
        when(itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(anyString(),
                anyString(), any())).thenReturn(List.of(ItemMapper.toItem(itemDto, user)));

        Collection<ItemDto> items = itemService.searchItem("book", 1, 1);

        assertEquals(1, items.size());
    }

    @Test
    void createComment_whenAllData_thenReturnSavedComment() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Good")
                .itemId(itemDto.getId())
                .authorName(user.getName())
                .created(LocalDateTime.now().minusMinutes(1))
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(commentRepository.save(any())).thenReturn(CommentMapper.toComment(commentDto, ItemMapper.toItem(itemDto,
                user), user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(ItemMapper.toItem(itemDto, user)));
        when(bookingService.findAllBookingsByBookerIdAndItemIdAndEndBeforeAndStatus(any(), any(), any(), any(), any()))
                .thenReturn(List.of(Booking.builder()
                        .item(ItemMapper.toItem(itemDto, user))
                        .booker(user)
                        .start(LocalDateTime.now().minusDays(1))
                        .end(LocalDateTime.now().minusHours(1))
                        .status(BookingStatus.APPROVED)
                        .build()));

        CommentDto actualCommentDto = itemService.createComment(user.getId(), commentDto);

        assertEquals(commentDto.getItemId(), actualCommentDto.getItemId());
        assertEquals(commentDto.getCreated(), actualCommentDto.getCreated());
        assertEquals(commentDto.getText(), actualCommentDto.getText());
    }

    @Test
    void createComment_whenBookingEqualsNull_thenThrowException() {
        CommentDto commentDto = CommentDto.builder()
                .id(1L)
                .text("Good")
                .itemId(itemDto.getId())
                .authorName(user.getName())
                .created(LocalDateTime.now().minusMinutes(1))
                .build();
        when(userService.getByIdOrNotFoundError(any())).thenReturn(user);
        when(commentRepository.save(any())).thenReturn(CommentMapper.toComment(commentDto, ItemMapper.toItem(itemDto,
                user), user));
        when(itemRepository.findById(any())).thenReturn(Optional.of(ItemMapper.toItem(itemDto, user)));
        when(bookingService.findAllBookingsByBookerIdAndItemIdAndEndBeforeAndStatus(any(), any(), any(), any(), any()))
                .thenReturn(null);

        BadRequestException badRequestException = assertThrows(BadRequestException.class,
                () -> itemService.createComment(user.getId(), commentDto));

        assertEquals("Comments are left after the end of the lease", badRequestException.getMessage());
    }
}