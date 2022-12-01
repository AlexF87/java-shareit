package ru.practicum.shareit.user;

import java.util.Collection;

public interface UserDao {
    User createUser(User user);

    Collection<User> getAllUsers();

    User getUserById(Long id);

    User update(Long id, User user);

    void deleteUser(Long id);

}
