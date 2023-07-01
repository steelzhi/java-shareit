package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.EmptyDescriptionException;
import ru.practicum.shareit.exception.RequestDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
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

    @Transactional
    @Override
    public ItemRequestDto postItemRequestDto(long userId, ItemRequestDto itemRequestDto) {
        checkIfDescriptionIsBlank(itemRequestDto);
        User requester = checkAndGetUserIfExists(userId);
        itemRequestDto.setRequesterDto(UserMapper.mapToUserDto(requester));
        itemRequestDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = ItemRequestMapper.mapToItemRequest(itemRequestDto);
        ItemRequest savedItemRequest = itemRequestRepository.save(itemRequest);
        itemRequestDto.setId(savedItemRequest.getId());
        return itemRequestDto;
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestDto> getAllRequestDtosMadeByRequester(long userId) {
        checkAndGetUserIfExists(userId);
        List<ItemRequest> requestersRequests = itemRequestRepository.findAllByRequester_Id(userId);
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : requestersRequests) {
            itemRequestDtos.add(
                    ItemRequestMapper.mapToItemRequestDto(
                            itemRequest, getAllProposedItemsForRequest(itemRequest.getId())));
        }

        return itemRequestDtos;
    }

    @Override
    public List<ItemRequestDto> getPagedRequestDtosMadeByOtherUsers(long userId, Integer from, Integer size) {
        List<ItemRequest> itemRequests = new ArrayList<>();
        if (from != null && size != null) {
            PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").ascending());
            Page<ItemRequest> pagedList = itemRequestRepository
                    .findAllByRequester_IdNot(userId, page);
            if (pagedList != null) {
                itemRequests = pagedList.getContent();
            }
        } else {
            itemRequests = itemRequestRepository.findAllByRequester_IdNot(userId);
        }
        List<ItemRequestDto> itemRequestDtos = getItemRequestDtos(itemRequests);
        return itemRequestDtos;
    }

    @Transactional(readOnly = true)
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

    private List<ItemDtoForSearch> getAllProposedItemsForRequest(long requestId) {
        List<Item> items = itemRepository.findAllByRequestId(requestId);
        List<ItemDtoForSearch> itemDtosForSearch = new ArrayList<>();
        for (Item item : items) {
            itemDtosForSearch.add(ItemMapper.mapToItemDtoForSearch(item));
        }

        return itemDtosForSearch;
    }

    private List<ItemRequestDto> getItemRequestDtos(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            List<ItemDtoForSearch> proposedItems = getAllProposedItemsForRequest(itemRequest.getId());
            ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, proposedItems);
            itemRequestDtos.add(itemRequestDto);
        }
        return itemRequestDtos;
    }

    private void checkIfDescriptionIsBlank(ItemRequestDto itemRequestDto) {
        if (itemRequestDto.getDescription() == null || itemRequestDto.getDescription().isBlank()) {
            throw new EmptyDescriptionException("Описание в запросе не должно быть пустым");
        }
    }
}
