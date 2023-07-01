package ru.practicum.shareit.exception;

public class BookingDoesNotExistException extends RuntimeException {
    public BookingDoesNotExistException(String message) {
        super(message);
    }
}