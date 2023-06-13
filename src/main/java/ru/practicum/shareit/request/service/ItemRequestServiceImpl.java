package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
        checkAndGetUserIfExists(userId);
        itemRequest.setRequesterId(userId);
        itemRequest.setCreated(LocalDateTime.now());
        return itemRequestRepository.save(itemRequest);
    }

    private User checkAndGetUserIfExists(long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new UserDoesNotExistException("Пользователя с таким id не существует"));
    }

    @Override
    public List<ItemRequestDto> getAllRequestsMadeByRequester(long userId) {
        List<ItemRequest> requestersRequests = itemRequestRepository.findAllByRequester_Id(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : requestersRequests) {
            List<Item> proposedItems = getAllProposedItemsForRequest(itemRequest.getId());
            ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, proposedItems);
            itemRequestDtos.add(itemRequestDto);
        }
        return itemRequestDtos;
    }

    private List<Item> getAllProposedItemsForRequest(long requestId) {
        return itemRepository.findAllByRequest_Id(requestId);
    }
}
