package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ItemRequestMapper {

    public static ItemRequestDto mapToItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getRequesterId(),
                itemRequest.getItemDescription(),
                itemRequest.getCreated(),
                items
        );
    }

/*    public static List<ItemRequestDto> mapToItemRequestDto(List<ItemRequest> itemRequests) {
        List<ItemRequestDto> itemRequestDtos = new ArrayList<>();
        for (ItemRequest itemRequest : itemRequests) {
            itemRequestDtos.add(mapToItemRequestDto())
        }
    }*/
}
