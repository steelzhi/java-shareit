package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String text;
    private ItemDtoForSearch itemDtoForSearch;
    private String authorName;
    private LocalDateTime created;
}
