package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemDtoRepository extends JpaRepository<ItemDto, Long> {

    @Modifying
    @Transactional
    @Query("UPDATE ItemDto AS itd " +
            "SET itd.name = COALESCE(?2, itd.name), " +
            "itd.description = COALESCE(?3, itd.description), " +
            "itd.available = COALESCE(?4, itd.available), " +
            "itd.request = COALESCE(?5, itd.request), " +
            "itd.owner = COALESCE(?6, itd.owner) " +
            "WHERE itd.id = ?1")
    int patchItemDto(Long itemId,
                   String name,
                   String description,
                   Boolean isAvailable,
                   Long request,
                   Long ownerId);

    List<ItemDto> findAllByOwnerId(Long ownerId);
}