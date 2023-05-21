package ru.practicum.shareit.utility;

import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.ItemDtoDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.model.User;

import java.util.List;
import java.util.Map;

public final class Checker {
    private Checker() {
    }

    public static void checkIfUserExists(Long userId, List<User> users) {
        for (User user : users) {
            if (user.getId() == userId) {
                return;
            }
        }

        throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
    }

    public static void checkIfUserAndItemExists(Long userId, Long itemDtoId, Map<Long, List<ItemDto>> items) {
        if (!items.containsKey(userId)) {
            throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
        }

        List<ItemDto> itemsDto = items.get(userId);
        for (ItemDto itemDto : itemsDto) {
            if (itemDto.getId() == itemDtoId) {
                return;
            }
        }

        throw new ItemDtoDoesNotExistException("Вещи с указанным id не существует.");
    }

    public static void checkIfEmailIsDuplicate(Long id, User user, List<User> users) {
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
            if (existingUser.getEmail().equals(email) && existingUser.getId() != id) {
                throw new DuplicateEmailException(
                        "Нельзя изменить email на указанный - пользователь с таким email уже существует.");
            }
        }
    }
}