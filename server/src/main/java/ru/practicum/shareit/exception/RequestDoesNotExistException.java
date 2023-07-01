package ru.practicum.shareit.exception;

public class RequestDoesNotExistException extends RuntimeException {
    public RequestDoesNotExistException(String message) {
        super(message);
    }
}