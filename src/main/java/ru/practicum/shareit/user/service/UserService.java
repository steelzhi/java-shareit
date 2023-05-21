package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> getUsers() {
        return userRepository.getUsers();
    }

    public User getUser(Long userId) {
        return userRepository.getUser(userId);
    }

    public User postUser(User user) {
        return userRepository.postUser(user);
    }

    public User patchUser(Long userId, User user) {
        return userRepository.patchUser(userId, user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUser(userId);
    }
}