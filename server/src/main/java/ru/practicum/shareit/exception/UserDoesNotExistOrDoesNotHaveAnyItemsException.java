package ru.practicum.shareit.exception;

public class UserDoesNotExistOrDoesNotHaveAnyItemsException extends RuntimeException {
    public UserDoesNotExistOrDoesNotHaveAnyItemsException(String message) {
        super(message);
    }
}