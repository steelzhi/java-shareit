package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@Builder
@Entity
@Table(name = "items")
@NoArgsConstructor
public class Item {

    @NotNull
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "is_available")
    private boolean isAvailable;

    @NotNull
    @Column(name = "user_id")
    private Long owner;

    @Column(name = "request_id")
    private Long request;
}