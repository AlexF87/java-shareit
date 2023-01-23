package ru.practicum.shareit.item;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.common.CustomPageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    ItemRepository itemRepository;
    @Autowired
    UserRepository userRepository;

    User user;
    Item item;

    @BeforeEach
    private void addItem() {
        user = User.builder()
                .name("Biil")
                .email("test@mail.test")
                .build();
        userRepository.save(user);
        itemRepository.save(Item.builder()
                .name("Book")
                .description("Java book")
                .available(true)
                .requestId(null)
                .owner(user)
                .build());
        itemRepository.save(Item.builder()
                .name("Boook")
                .description("JavaScript book")
                .available(true)
                .requestId(null)
                .owner(user)
                .build());
    }

    @Test
    void findByOwner_Id() {
        List<Item> itemList = itemRepository.findByOwner_IdOrderByIdAsc(user.getId(),
                CustomPageRequest.of(0, 10));

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @Test
    void findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue() {
        List<Item> itemList =
                itemRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailableIsTrue(
                        "Java", "Java", CustomPageRequest.of(0, 2));

        assertNotNull(itemList);
        assertEquals(2, itemList.size());
    }

    @AfterEach
    private void deleteItems() {
        itemRepository.deleteAll();
    }
}