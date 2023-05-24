package ru.practicum.shareit.exception;

public class ItemDtoDoesNotExistException extends RuntimeException {
    public ItemDtoDoesNotExistException(String message) {
        super(message);
    }
}