package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

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
        return userRepository.getReferenceById(userId);
    }

    @Override
    public User postUser(User user) {
        checkIfEmailIsDuplicate(null, user, getUsers());
        return userRepository.save(user);
    }

    @Override
    public User patchUser(Long id, User user) {
        checkIfEmailIsDuplicate(id, user, getUsers());
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

    @Override
    public void checkIfEmailIsDuplicate(Long id, User user, List<User> users) {
        String email = user.getEmail();

        // Проверка совпадения для нового пользователя (при добавлении пользователя)
        if (id == null) {
            for (User existingUser : users) {
                if (existingUser.getEmail().equals(email)) {
                    throw new DuplicateEmailException("Пользователь с указанным email уже существует.");
                }
            }
        }

        // Проверка совпадения для существующего пользователя (при изменении пользователя)
        for (User existingUser : users) {
            if (existingUser.getEmail().equals(email) && !existingUser.getId().equals(id)) {
                throw new DuplicateEmailException(
                        "Нельзя изменить email на указанный - пользователь с таким email уже существует.");
            }
        }
    }
}