package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceImplTest {
    private UserServiceImpl userService;
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository = mock(UserRepository.class);
        when(userRepository.save(any())).then(i -> i.getArgument(0));

        userService = new UserServiceImpl(userRepository);
    }

    @Test
    void getUser_whenUserFound_thenReturnUser() {
        User user = User.builder()
                .id(1L)
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUser(user.getId());

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
        assertEquals(user.getId(), result.getId());
    }

    @Test
    void getUser_whenUserNotFound_thenReturnNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                () -> userService.getUser(0L));
        assertEquals("User not exists", notFoundException.getMessage());
    }

    @Test
    void getUsers_whenGetUsers_thenReturnListUsers() {
        when(userRepository.findAll()).thenReturn(Collections.emptyList());

        Collection<UserDto> users = userService.getUsers();

        assertEquals(0, users.size());
        verify(userRepository, times(1)).findAll();

    }

    @Test
    void createUser_whenUserValid_thenSavedUser() {
        UserDto userDto = UserDto.builder()
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        User user = User.builder()
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        when(userRepository.save(user)).thenReturn(user);

        UserDto result = userService.createUser(userDto);

        assertEquals(userDto, result);
        verify(userRepository).save(UserMapper.toUser(userDto));
    }

    @Test
    void updateUser_whenUserFound_thenUpdateFields() {
        User user = User.builder()
                .id(1L)
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        UserDto updUser = UserDto.builder()
                .id(1L)
                .name("updUser")
                .email("updUser@tester.ru")
                .build();
        when(userRepository.existsById(user.getId())).thenReturn(true);
        when(userRepository.getReferenceById(user.getId())).thenReturn(user);

        UserDto actualUser = userService.updateUser(user.getId(), updUser);

        assertEquals("updUser", actualUser.getName());
        assertEquals("updUser@tester.ru", actualUser.getEmail());
    }

    @Test
    void updateUser_whenUserNotFound_thenReturnBadRequestException() {
        User user = User.builder()
                .id(1L)
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        UserDto updUser = UserDto.builder()
                .id(1L)
                .name("updUser")
                .email("updUser@tester.ru")
                .build();
        when(userRepository.existsById(user.getId())).thenReturn(false);

        BadRequestException badRequestException =
                assertThrows(BadRequestException.class,
                        () -> userService.updateUser(user.getId(), updUser));

        assertEquals("User not exist, id: " + user.getId(), badRequestException.getMessage());
    }

    @Test
    void deleteUserById() {
        UserDto user = UserDto.builder()
                .id(1L)
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        UserDto saveUser = userService.createUser(user);

        userService.deleteUserById(saveUser.getId());

        verify(userRepository, times(1)).deleteById(saveUser.getId());
    }

    @Test
    void getByIdOrNotFoundError_whenUserFound_thenReturnUser() {
        User user = User.builder()
                .id(1L)
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        User findUser = userService.getByIdOrNotFoundError(user.getId());

        assertEquals(user.getName(), findUser.getName());
        assertEquals(user.getEmail(), findUser.getEmail());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getByIdOrNotFoundError_whenUserNotFound_thenNotFoundException() {
        User user = User.builder()
                .id(1L)
                .name("TestUser")
                .email("tester@tester.ru")
                .build();
        when(userRepository.findById(user.getId())).thenReturn(Optional.empty());

        NotFoundException notFoundException = assertThrows(NotFoundException.class,
                        () -> userService.getByIdOrNotFoundError(user.getId()));

        assertEquals(String.format("Not found user, id: %d ", user.getId()), notFoundException.getMessage());
        verify(userRepository, times(1)).findById(user.getId());
    }
}