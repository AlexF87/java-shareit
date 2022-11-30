package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.EmailException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public UserDto getUser(Long userId) {
        return UserMapper.toUserDto(userDao.getUserById(userId));
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userDao.getAllUsers().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        if (checkUserMailForUniqueness(userDto.getEmail())) {
            throw new EmailException(String.format("A user with such an  email %s already exists",
                    userDto.getEmail()));
        }
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userDao.createUser(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        UserDto userDtoFromDB = getUser(userId);
        User userDB = UserMapper.toUser(userDtoFromDB);
        if (userDto.getName() != null) {
            userDB.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            if (!checkUserMailForUniqueness(userDto.getEmail())) {
                userDB.setEmail(userDto.getEmail());
            } else {
                throw new EmailException(String.format("Email: %s already exists", userDto.getEmail()));
            }
        }
        return UserMapper.toUserDto(userDao.update(userId, userDB));
    }

    @Override
    public void deleteUserById(Long userId) {
        userDao.deleteUser(userId);
    }

    private boolean checkUserMailForUniqueness(String email) {
        return userDao.getAllUsers().stream().map(User::getEmail).anyMatch(x -> x.equals(email));
    }
}
