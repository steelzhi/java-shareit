package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.item.dto.ItemDtoForSearch;
import ru.practicum.shareit.item.mapper.ItemMapper;
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
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    ItemRequestRepository itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
    UserRepository userRepository = Mockito.mock(UserRepository.class);
    ItemRepository itemRepository = Mockito.mock(ItemRepository.class);

    ItemRequestService itemRequestService = new ItemRequestServiceImpl(
            itemRequestRepository,
            userRepository,
            itemRepository);

    User user1 = User.builder()
            .id(1L)
            .name("user1")
            .email("user1@user.ru")
            .build();

    User user2 = User.builder()
            .id(2L)
            .name("user2")
            .email("user2@user.ru")
            .build();

    Item item1 = Item.builder()
            .id(1L)
            .name("УШМ")
            .description("Углошлифовальная машина")
            .available(true)
            .owner(user1)
            .build();

    ItemDtoForSearch itemDtoForSearch1 = ItemMapper.mapToItemDtoForSearch(item1);

    LocalDateTime now = LocalDateTime.now();

    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .requester(user2)
            .description("Хотел бы воспользоваться УШМ")
            .created(now)
            .build();

    ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, List.of(itemDtoForSearch1));

    @Test
    void postItemRequest() {
        Mockito.when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        itemRequest.setRequester(userRepository.findById(user2.getId()).get());
        Mockito.when(itemRequestRepository.save(itemRequest))
                .thenReturn(itemRequest);

        ItemRequestDto savedItemRequestDto = ItemRequestMapper
                .mapToItemRequestDto(itemRequestRepository.save(itemRequest), List.of(itemDtoForSearch1));
        savedItemRequestDto.setCreated(itemRequestDto.getCreated());
        assertThat(itemRequestDto, equalTo(savedItemRequestDto));

        Mockito.verify(userRepository, Mockito.times(1)).findById(user2.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(itemRequest);
    }

    @Test
    void postItemRequestByNotExistingRequester() {
        Mockito.when(userRepository.findById(100L))
                .thenThrow(new UserDoesNotExistException("Пользователя с таким id не существует"));

        UserDoesNotExistException userDoesNotExistException = assertThrows(UserDoesNotExistException.class,
                () -> itemRequestService.postItemRequestDto(100L, itemRequestDto));
        assertThat(userDoesNotExistException.getMessage(), equalTo("Пользователя с таким id не существует"));

        Mockito.verify(userRepository, Mockito.times(1)).findById(100L);
        Mockito.verify(itemRequestRepository, Mockito.never()).save(itemRequest);
    }

    @Test
    void getAllRequestsMadeByRequester() {
        Mockito.when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRequestRepository.findAllByRequester_Id(user2.getId()))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of(item1));

        List<ItemRequestDto> allItemRequestsByRequesterId2 =
                itemRequestService.getAllRequestDtosMadeByRequester(user2.getId());
        assertThat(allItemRequestsByRequesterId2, equalTo(List.of(itemRequestDto)));

        Mockito.verify(userRepository, Mockito.times(1)).findById(user2.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequester_Id(user2.getId());
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByRequestId(1L);
    }

    @Test
    void getPagedRequestsMadeByOtherUsers() {
        Page<ItemRequest> pagedList = new PageImpl(List.of(itemRequest));
        Mockito.when(itemRequestRepository.findAllByRequester_IdNot(2L, getPage(0, 2)))
                .thenReturn(pagedList);
        Mockito.when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of(item1));

        List<ItemRequestDto> itemRequestDtos =
                itemRequestService.getPagedRequestDtosMadeByOtherUsers(2L, 0, 2);

        List<ItemRequestDto> mappedItemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : pagedList.getContent()) {
            List<Item> proposedItems = itemRepository.findAllByRequestId(itemRequest.getId());
            List<ItemDtoForSearch> itemDtoForSearches = new ArrayList<>();
            for (Item item : proposedItems) {
                itemDtoForSearches.add(ItemMapper.mapToItemDtoForSearch(item));
            }
            ItemRequestDto itemRequestDto = ItemRequestMapper.mapToItemRequestDto(itemRequest, itemDtoForSearches);
            mappedItemRequestDtos.add(itemRequestDto);
        }

        assertThat(itemRequestDtos, equalTo(mappedItemRequestDtos));

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequester_IdNot(2L, getPage(0, 2));
        Mockito.verify(itemRepository, Mockito.times(2)).findAllByRequestId(1L);
    }

    @Test
    void getUnPagedRequestsMadeByOtherUsers() {
        Mockito.when(itemRequestRepository.findAllByRequester_IdNot(2L))
                .thenReturn(List.of(itemRequest));
        Mockito.when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of(item1));

        List<ItemRequestDto> itemRequestDtos =
                itemRequestService.getPagedRequestDtosMadeByOtherUsers(2L, null, null);

        assertThat(itemRequestDtos,
                equalTo(List.of(ItemRequestMapper
                        .mapToItemRequestDto(itemRequest, List.of(ItemMapper.mapToItemDtoForSearch(item1))))));

        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findAllByRequester_IdNot(2L);
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByRequestId(1L);
    }

    @Test
    void getRequestDto() {
        Mockito.when(userRepository.findById(user2.getId()))
                .thenReturn(Optional.of(user2));
        Mockito.when(itemRepository.findAllByRequestId(1L))
                .thenReturn(List.of(item1));
        Mockito.when(itemRequestRepository.findById(1L))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestDto retrievedItemRequestDto = itemRequestService.getRequestDto(2L, 1L);

        assertThat(retrievedItemRequestDto,
                equalTo(ItemRequestMapper
                        .mapToItemRequestDto(itemRequest, List.of(ItemMapper.mapToItemDtoForSearch(item1)))));

        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(1L);
    }

    PageRequest getPage(Integer from, Integer size) {
        if (from != null && size != null) {
            return PageRequest.of(from > 0 ? from / size : 0, size, Sort.by("created").ascending());
        }
        return null;
    }
}
