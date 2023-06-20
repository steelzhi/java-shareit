package ru.practicum.shareit.item.mapper;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.ArrayList;
import java.util.List;

public class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto mapToCommentDto(Comment comment) {
        CommentDto commentDto = null;
        if (comment != null) {
            commentDto = new CommentDto(
                    comment.getId(),
                    comment.getText(),
                    ItemMapper.mapToItemDtoForSearch(comment.getItem()),
                    comment.getAuthor().getName(),
                    comment.getCreated()
            );
        }
        return commentDto;
    }

    public static List<CommentDto> mapToCommentDto(List<Comment> comments) {
        List<CommentDto> commentDtos = new ArrayList<>();
        if (comments != null) {
            for (Comment comment : comments) {
                commentDtos.add(mapToCommentDto(comment));
            }
        }
        return commentDtos;
    }
}