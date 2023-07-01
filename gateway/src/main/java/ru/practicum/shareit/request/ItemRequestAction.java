package ru.practicum.shareit.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Data
@AllArgsConstructor
@Builder
public class ItemRequestAction {
    private Actions action;
    private ResponseEntity<Object> lastResponse;
    private long userId;
    private long requesterId;
    private long otherUserId;
    private ItemRequestDto itemRequestDto;
    private Integer from;
    private Integer size;
    private long requestId;
}
