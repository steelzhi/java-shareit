package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
public class Item {

    @NotNull

    private Long id;

    @NotBlank

    private String name;


    private String description;


    private Boolean isAvailable;

    @NotNull

    private User owner;


    private Long request;
}