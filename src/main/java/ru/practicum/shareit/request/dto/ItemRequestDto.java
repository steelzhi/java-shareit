package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
public class ItemRequestDto {
    private Long id;
    private Long requesterId;
    private String itemDescription;
    private LocalDateTime created;
    private List<Item> items;
}