package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.repository.ItemDtoRepository;

import java.util.ArrayList;
import java.util.List;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemDtoRepository itemRepository;

    @Override
    public ItemDto postItemDto(ItemDto itemDto, Long userId) {
        return itemRepository.postItemDto(itemDto, userId);
    }

    @Override
    public ItemDto patchItemDto(Long itemId, ItemDto itemDto, Long userId) {
        return itemRepository.patchItemDto(itemId, itemDto, userId);
    }

    @Override
    public ItemDto getItemDto(Long itemId) {
        return itemRepository.getItemDto(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsDtoByUser(Long userId) {
        return itemRepository.getAllItemsDtoByUser(userId);
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        List<ItemDto> foundBySearch = new ArrayList<>();

        if (!text.isBlank()) {
            List<ItemDto> allItemsDto = itemRepository.getAllItemsDto();

            for (ItemDto itemDto : allItemsDto) {
                if ((itemDto.getName().toLowerCase().contains(text.toLowerCase())
                        || itemDto.getDescription().toLowerCase().contains(text.toLowerCase()))
                        && itemDto.getAvailable() == true) {
                    foundBySearch.add(itemDto);
                }
            }
        }

        return foundBySearch;
    }
}
