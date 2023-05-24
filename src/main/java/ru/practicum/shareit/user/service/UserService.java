package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserService {
    List<User> getUsers();

    User getUser(Long userId);

    User postUser(User user);

    User patchUser(Long id, User user);

    void deleteUser(Long userId);

    void checkIfEmailIsDuplicate(Long id, User user, List<User> users);
}