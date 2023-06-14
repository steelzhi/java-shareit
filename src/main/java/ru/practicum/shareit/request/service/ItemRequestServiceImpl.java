package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmptyDescriptionException;
import ru.practicum.shareit.exception.IncorrectPaginationException;
import ru.practicum.shareit.exception.RequestDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.Pagination;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequest postItemRequest(long userId, ItemRequest itemRequest) {
        checkIfDescriptionIsBlank(itemRequest);
        User requester = checkAndGetUserIfExists(userId);
        itemRequest.setRequester(requester);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsMadeByRequester(long userId) {
        checkAndGetUserIfExists(userId);
        List<ItemRequest> requestersRequests = itemRequestRepository.findAllByRequester_Id(userId);
        List<ItemRequestDto> itemRequestDtos = getItemRequestDtos(requestersRequests);
        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getPagedRequestsMadeByOtherUsers(long userId, Integer from, Integer size) {
        Pagination.checkIfPaginationParamsAreNotCorrect(from, size);

        List<ItemRequest> itemRequests;
        if (from != null && size != null) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").ascending());
            itemRequests = itemRequestRepository
                    .findAllByRequester_IdNot(userId, page)
                    .getContent();
        } else {
            itemRequests = itemRequestRepository.findAllByRequester_IdNot(userId);
        }
        List<ItemRequestDto> itemRequestDtos = getItemRequestDtos(itemRequests);
        return itemRequestDtos;
    }

    @Override
    public ItemRequestDto getRequestDto(long userId, long requestId) {
        checkAndGetUserIfExists(userId);
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new RequestDoesNotExistException("Запроса с указанным id не существует"));
        return ItemRequestMapper.mapToItemRequestDto(itemRequest, getAllProposedItemsForRequest(requestId));
    }

    private User checkAndGetUserIfExists(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Пользователя с таким id не существует"));
    }

    private List<Item> getAllProposedItemsForRequest(long requestId) {
        return itemRepository.findAllByRequestId(requestId);
    }

    private List<ItemRequestDto> getItemRequestDtos(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<Item> proposedItems = getAllProposedItemsForRequest(itemRequest.getId());
            ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, proposedItems);
            itemRequestDtos.add(itemRequestDto);
        }
        return itemRequestDtos;
    }

    private void checkIfDescriptionIsBlank(ItemRequest itemRequest) {
        if (itemRequest.getDescription() == null || itemRequest.getDescription().isBlank()) {
            throw new EmptyDescriptionException("Описание в запросе не должно быть пустым");
        }
    }
}
