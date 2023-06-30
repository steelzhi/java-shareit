package ru.practicum.shareit.exception;

public class PostCommentProhibitedException extends RuntimeException {
    public PostCommentProhibitedException(String message) {
        super(message);
    }
}