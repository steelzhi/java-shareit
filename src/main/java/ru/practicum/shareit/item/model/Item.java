package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */

@Data
@AllArgsConstructor
@Builder
public class Item {
    @NotNull
    private Long id;
    @NotBlank
    private String name;
    private String description;
    private boolean isAvailable;
    @NotNull
    private Long owner;
    private Long request;
}