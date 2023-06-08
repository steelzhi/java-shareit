package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUser(long userId);

    User postUser(User user);

    User patchUser(long id, User user);

    void deleteUser(long userId);
}