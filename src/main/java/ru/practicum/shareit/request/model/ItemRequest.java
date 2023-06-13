package ru.practicum.shareit.request.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "item_request")
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer"})
public class ItemRequest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @NotBlank
    @Column(name = "requester_id")
    private Long requesterId;

    @NotBlank
    @Column(name = "item_description")
    private String itemDescription;

    @NotNull
    @Column(name = "created")
    private LocalDateTime created;
}