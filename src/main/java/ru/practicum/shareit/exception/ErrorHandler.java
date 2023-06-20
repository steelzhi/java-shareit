package ru.practicum.shareit.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserDoesNotExist(final UserDoesNotExistException e) {
        return new ErrorResponse("Пользователь не найден", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleUserDoesNotExistOrDoesNotHaveAnyItems(
            final UserDoesNotExistOrDoesNotHaveAnyItemsException e) {
        return new ErrorResponse("Пользователь не найден либо у пользователя не добавлено ни одной вещи",
                e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemNotAvailable(final ItemNotAvailableException e) {
        return new ErrorResponse("Вещь занята", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleItemNotFound(final ItemDoesNotExistException e) {
        return new ErrorResponse("Вещь не найдена", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleItemIncorrectDate(final IncorrectDateException e) {
        return new ErrorResponse("Некорректная дата аренды", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleWrongBookingStatus(final WrongBookingStatusException e) {
        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleBookingDoesNotExist(final BookingDoesNotExistException e) {
        return new ErrorResponse("Бронирование не найдено", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalAccess(final IllegalAccessException e) {
        return new ErrorResponse("Вы не имеете прав на просмотр этого бронирования", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDuplicateStatus(final DuplicateStatusException e) {
        return new ErrorResponse("Данный статус уже установлен", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleIllegalBookingAttempt(final IllegalBookingAttemptException e) {
        return new ErrorResponse("Владелец вещи не может бронировать свою вещь", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handlePostCommentProhibited(final PostCommentProhibitedException e) {
        return new ErrorResponse("Вы не можете оставить комментарий к этой вещи", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmptyComment(final EmptyCommentException e) {
        return new ErrorResponse("Комментарий не может быть пустым", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleRequestDoesNotExist(final RequestDoesNotExistException e) {
        return new ErrorResponse("Запрос не найден", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleEmptyDescription(final EmptyDescriptionException e) {
        return new ErrorResponse("Описание запроса не может быть пустым", e.getMessage());
    }

    @ExceptionHandler()
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIncorrectPagination(final IncorrectPaginationException e) {
        return new ErrorResponse("Параметры пагинации должны быть >= 0", e.getMessage());
    }
}