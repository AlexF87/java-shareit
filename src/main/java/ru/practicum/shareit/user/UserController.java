package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {
    private final UserService service;

    @GetMapping
    public Collection<UserDto> getUsers() {
        return service.getUsers();
    }

    @DeleteMapping("/{id}")
    public void deleteUserById(@PathVariable Long id) {
        service.deleteUserById(id);
    }

    @GetMapping("/{id")
    public UserDto getUser(@PathVariable Long id) {
        return service.getUser(id);
    }

    @PostMapping
    public UserDto addUser(@Valid @RequestBody UserDto userDto) {
        return service.createUser(userDto);
    }

    @PatchMapping("/id")
    public UserDto updateUser(@PatchVariable Long id, @RequestBody UserDto userDto) {
        return service.updateUser(id, user);
    }
}
