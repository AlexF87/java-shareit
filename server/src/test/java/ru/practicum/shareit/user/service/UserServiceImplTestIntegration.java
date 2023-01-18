package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.Collection;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class UserServiceImplTestIntegration {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;

    UserDto user;

    @BeforeEach
    void setUp() {
        user = UserDto.builder()
                .name("Bill")
                .email("test@mail.ty")
                .build();
        userService.createUser(user);
    }

    @Test
    void getUsers() {
        Collection<UserDto> userDtoList = userService.getUsers();

        assertEquals(1, userDtoList.size());
        assertEquals(user.getName(), userDtoList.stream().findFirst().get().getName());
        assertEquals(user.getEmail(), userDtoList.stream().findFirst().get().getEmail());
    }

    @Test
    void uppdateUser() {
        UserDto newUser = UserDto.builder()
                .email("bill@mail.ty")
                .build();
        Long userId = 1L;

        UserDto actualUser = userService.updateUser(userId, newUser);

        assertEquals(userId, actualUser.getId());
        assertEquals(newUser.getEmail(), actualUser.getEmail());
        assertNotEquals(user.getEmail(), actualUser.getEmail());
    }

    @AfterEach
    void deleteAll() {
        userRepository.deleteAll();
    }
}