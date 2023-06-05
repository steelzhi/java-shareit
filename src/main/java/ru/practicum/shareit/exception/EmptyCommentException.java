package ru.practicum.shareit.exception;

public class EmptyCommentException extends RuntimeException {
    public EmptyCommentException(String message) {
        super(message);
    }
}