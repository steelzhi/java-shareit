package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.item.dto.ItemDto;

@Data
@AllArgsConstructor
@Builder
public class ItemAction {
    private Actions action;
    private ResponseEntity<Object> lastResponse;
    private long userId;
    private long itemId;
    private ItemDto itemDto;
    private Integer from;
    private Integer size;
    private String text;
}
