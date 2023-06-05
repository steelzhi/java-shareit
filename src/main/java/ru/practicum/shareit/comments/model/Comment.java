package ru.practicum.shareit.comments.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "comments")
@JsonIgnoreProperties({"hibernateLazyInitializer"})
@Builder
public class Comment {

    @Id
    @GeneratedValue
    private Long id;

    @NotBlank
    @Column(name = "text")
    private String text;

    @NotNull
    @JoinColumn(name = "item_id")
    private Item item;

    @NotNull
    @JoinColumn(name = "author_id")
    private User author;

    @NotNull
    @Column(name = "created")
    private LocalDateTime created;

}
