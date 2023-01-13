package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ItemServiceImplTestIntegration {
    @Autowired
    private UserService userService;
    @Autowired
    private ItemService itemService;
    @Autowired
    private ItemRepository itemRepository;

    UserDto user;
    ItemDto item;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name("Dima")
                .email("dima@rambler.rt")
                .build();
        user = userService.createUser(user);

        item = ItemDto.builder()
                .name("book")
                .description("Java")
                .available(true)
                .owner(user.getId())
                .build();
        item = itemService.addItem(user.getId(), item);
    }

    @Test
    void getAllItems() {

        List<ItemDto> itemDtoList = itemService.getAllItems(user.getId(), 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
    }

    @Test
    void searchItem() {
        Collection<ItemDto> itemDtoList = itemService.searchItem("JAVA", 0, 10);

        assertNotNull(itemDtoList);
        assertEquals(1, itemDtoList.size());
        assertEquals(item.getDescription(), itemDtoList.stream().findFirst().get().getDescription());
    }
}