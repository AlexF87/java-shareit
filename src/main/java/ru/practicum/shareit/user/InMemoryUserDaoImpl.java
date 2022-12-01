package ru.practicum.shareit.user;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.handler.exception.NotFoundException;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Component
public class InMemoryUserDaoImpl implements UserDao {
    private final Map<Long, User> users = new HashMap<>();
    private static long idCounter = 0L;

    @Override
    public User createUser(User user) {
        user.setId(++idCounter);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public Collection<User> getAllUsers() {
        return users.values();
    }

    @Override
    public User getUserById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException(String.format("The user with the id %d is not in DB", id));
        }
        return users.get(id);
    }

    @Override
    public User update(Long id, User user) {
        users.put(id, user);
        return users.get(id);
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }
}
