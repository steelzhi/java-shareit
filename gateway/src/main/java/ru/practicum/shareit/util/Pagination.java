package ru.practicum.shareit.util;

import ru.practicum.shareit.exception.IncorrectPaginationException;

public class Pagination {
    private Pagination() {
    }

    public static void checkIfPaginationParamsAreNotCorrect(Integer from, Integer size) {
        if ((from == null && size == null)
                || (from >= 0 && size > 0)) {
            return;
        }

        throw new IncorrectPaginationException("Введены некорректные параметры для пагинации");
    }
}
