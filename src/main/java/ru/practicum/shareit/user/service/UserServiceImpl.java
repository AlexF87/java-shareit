package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.handler.exception.BadRequestException;
import ru.practicum.shareit.handler.exception.EmailException;
import ru.practicum.shareit.handler.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserDao;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;

import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public UserDto getUser(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            throw new NotFoundException(String.format("User not exists"));
        }
        return UserMapper.toUserDto(user.get());
    }

    @Override
    public Collection<UserDto> getUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        User user = UserMapper.toUser(userDto);
        return UserMapper.toUserDto(userRepository.save(user));
    }

    @Override
    public UserDto updateUser(Long userId, UserDto userDto) {
        User userDB = new User();
        if (userRepository.existsById(userId)) {
            UserDto userDtoFromDB = UserMapper.toUserDto(userRepository.getReferenceById(userId));
            userDB = UserMapper.toUser(userDtoFromDB);
        } else {
            throw new BadRequestException(String.format("User not exist, id: %d", userId));
        }
        if (userDto.getName() != null) {
            userDB.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            userDB.setEmail(userDto.getEmail());
        }
        return UserMapper.toUserDto(userRepository.save(userDB));
    }

    @Override
    public void deleteUserById(Long userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public User getByIdOrNotFoundError(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Not found user, id: %d ", userId)));
    }

}
