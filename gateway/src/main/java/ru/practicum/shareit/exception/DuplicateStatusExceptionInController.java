package ru.practicum.shareit.exception;

public class DuplicateStatusExceptionInController extends RuntimeException {
    public DuplicateStatusExceptionInController(String message) {
        super(message);
    }
}