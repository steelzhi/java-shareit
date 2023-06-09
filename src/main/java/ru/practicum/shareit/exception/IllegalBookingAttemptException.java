package ru.practicum.shareit.exception;

public class IllegalBookingAttemptException extends RuntimeException {
    public IllegalBookingAttemptException(String message) {
        super(message);
    }
}