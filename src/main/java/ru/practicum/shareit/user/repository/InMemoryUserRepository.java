package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
@Repository
public class InMemoryUserRepository implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static Long id = 1L;

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getUser(Long userId) {
        if (!users.containsKey(userId)) {
            throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
        }

        return users.get(userId);
    }

    @Override
    public User postUser(User user) {
        user.setId(id++);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User patchUser(Long userId, User user) {
        if (!users.containsKey(userId)) {
            throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
        }

        User existingUser = users.get(userId);
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }

        return existingUser;
    }

    @Override
    public void deleteUser(Long userId) {
        users.remove(userId);
    }

    @Override
    public void deleteAllUsers() {
        users.clear();
    }
}*/
