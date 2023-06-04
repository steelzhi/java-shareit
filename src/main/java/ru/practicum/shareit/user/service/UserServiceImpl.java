package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    @Override
    public List<User> getUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long userId) {
        List<User> users = getUsers();
        for (User user : users) {
            if (user.getId() == userId) {
                return user;
            }
        }

        throw new UserDoesNotExistException("Пользователя с id = " + userId + " не существует.");
    }

    @Override
    public User postUser(User user) {
        return userRepository.save(user);
    }


    @Override
    public User patchUser(Long id, User user) {
        User existingUser = getUser(id);
        userRepository.patchUser(id, user.getName(), user.getEmail());
        user.setId(id);
        if (user.getName() == null) {
            user.setName(existingUser.getName());
        }
        if (user.getEmail() == null) {
            user.setEmail(existingUser.getEmail());
        }
        return user;
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        userRepository.deleteAllByIdInBatch(List.of(userId));
    }
}