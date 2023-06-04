package ru.practicum.shareit.exception;

public class UserDoesNotHaveItemsException extends RuntimeException {
    public UserDoesNotHaveItemsException(String message) {
        super(message);
    }
}