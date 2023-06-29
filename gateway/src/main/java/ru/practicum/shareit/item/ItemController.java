package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.util.Pagination;

import javax.validation.Valid;
import javax.websocket.server.PathParam;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {
    private final ItemClient itemClient;
    private ItemAction lastAction;

    @PostMapping
    public ResponseEntity<Object> postItemDto(@Valid @RequestBody ItemDto itemDto,
                                              @RequestHeader("X-Sharer-User-Id") long userId) {
        ResponseEntity<Object> result = itemClient.postItemDto(itemDto, userId);
        lastAction = ItemAction.builder()
                .action(Actions.POST)
                .lastResponse(result)
                .itemDto(itemDto)
                .userId(userId)
                .build();

        return result;
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> patchItemDto(@PathVariable long itemId,
                                               @RequestBody ItemDto itemDto,
                                               @RequestHeader("X-Sharer-User-Id") long userId) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.PATCH)
                    && lastAction.getItemId() == itemId
                    && itemDto.equals(lastAction.getItemDto())
                    && lastAction.getUserId() == userId) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = itemClient.patchItemDto(itemId, itemDto, userId);
        lastAction = ItemAction.builder()
                .action(Actions.PATCH)
                .lastResponse(result)
                .itemId(itemId)
                .itemDto(itemDto)
                .userId(userId)
                .build();

        return result;
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemDtoById(@PathVariable long itemId,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getItemId() == itemId
                    && lastAction.getUserId() == userId) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = itemClient.getItemDtoById(itemId, userId);
        lastAction = ItemAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .itemId(itemId)
                .userId(userId)
                .build();

        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getAllItemsDtoByUser(@RequestHeader("X-Sharer-User-Id") long userId,
                                                       @RequestParam(value = "from", required = false) Integer from,
                                                       @RequestParam(value = "size", required = false) Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getUserId() == userId
                    && from.equals(lastAction.getFrom())
                    && size.equals(lastAction.getSize())) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = itemClient.getAllItemsDtoByUser(userId, from, size);
        lastAction = ItemAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .userId(userId)
                .from(from)
                .size(size)
                .build();

        return result;
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItemDto(@PathParam("text") String text,
                                                @RequestParam(value = "from", required = false) Integer from,
                                                @RequestParam(value = "size", required = false) Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && text.equals(lastAction.getText())
                    && from.equals(lastAction.getFrom())
                    && size.equals(lastAction.getSize())) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = itemClient.searchItemDto(text, from, size);
        lastAction = ItemAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .text(text)
                .from(from)
                .size(size)
                .build();

        return result;
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postCommentDto(@PathVariable long itemId,
                                                 @RequestBody CommentDto commentDto,
                                                 @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemClient.postCommentDto(itemId, commentDto, userId);
    }
}