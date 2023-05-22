package ru.practicum.shareit.user.repository;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserRepository {
    List<User> getUsers();

    User getUser(Long userId);

    User postUser(User user);

    User patchUser(Long userId, User user);

    void deleteUser(Long userId);

    void deleteAllUsers();
}