package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.actions.Actions;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.util.Pagination;

@Controller
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;
    private ItemRequestAction lastAction;

    @PostMapping
    public ResponseEntity<Object> postItemRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                     @RequestBody ItemRequestDto itemRequestDto) {
        ResponseEntity<Object> result = itemRequestClient.postItemRequest(userId, itemRequestDto);
        lastAction = ItemRequestAction.builder()
                .action(Actions.POST)
                .lastResponse(result)
                .userId(userId)
                .itemRequestDto(itemRequestDto)
                .build();

        return result;
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsMadeByRequester(@RequestHeader("X-Sharer-User-Id") long userId) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getRequesterId() == userId
                    && lastAction.getFrom() == null
                    && lastAction.getSize() == null
                    && lastAction.getRequesterId() == 0L) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = itemRequestClient.getAllRequestsMadeByRequester(userId);
        lastAction = ItemRequestAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .requesterId(userId)
                .build();

        return result;
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getPagedRequestsMadeByOtherUsers(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestParam(value = "from", required = false) Integer from,
            @RequestParam(value = "size", required = false) Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getOtherUserId() == userId
                    && from.equals(lastAction.getFrom())
                    && size.equals(lastAction.getSize())) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = itemRequestClient.getPagedRequestsMadeByOtherUsers(userId, from, size);
        lastAction = ItemRequestAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .otherUserId(userId)
                .from(from)
                .size(size)
                .build();

        return result;
    }

    @GetMapping("{requestId}")
    public ResponseEntity<Object> getRequest(@RequestHeader("X-Sharer-User-Id") long userId,
                                                @PathVariable long requestId) {
        if (lastAction != null) {
            if (lastAction.getAction().equals(Actions.GET)
                    && lastAction.getUserId() == userId
                    && lastAction.getRequestId() == requestId) {
                return lastAction.getLastResponse();
            }
        }

        ResponseEntity<Object> result = itemRequestClient.getRequest(userId, requestId);
        lastAction = ItemRequestAction.builder()
                .action(Actions.GET)
                .lastResponse(result)
                .userId(userId)
                .requestId(requestId)
                .build();

        return result;
    }
}