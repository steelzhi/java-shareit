package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.IllegalAccessException;
import ru.practicum.shareit.exception.ItemDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistException;
import ru.practicum.shareit.exception.UserDoesNotExistOrDoesNotHaveAnyItemsException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public Item postItemDto(Item item, Long userId) {
        checkIfUserExists(userId, userRepository.findAll());
        item.setOwner(userRepository.findById(userId).get());
        return itemRepository.save(item);
    }

    @Override
    public Item patchItemDto(Long itemId, Item item, Long userId) {
        checkIfUserAndItemExists(userId, itemId);
        checkIfUserHasRightToPatchOrGetBookings(itemId, userId);
        item.setId(itemId);
        Item existingItemDto = getItem(itemId);
        if (item.getName() == null) {
            item.setName(existingItemDto.getName());
        }
        if (item.getDescription() == null) {
            item.setDescription(existingItemDto.getDescription());
        }
        if (item.getAvailable() == null) {
            item.setAvailable(existingItemDto.getAvailable());
        }
        if (item.getRequest() == null) {
            item.setRequest(existingItemDto.getRequest());
        }
        if (item.getOwner() == null) {
            item.setOwner(existingItemDto.getOwner());
        }

        return itemRepository.save(item);
    }

    @Override
    public ItemDto getItemDtoById(Long itemId, Long userId) {
        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            if (item.getId() == itemId) {
                return getItemDtoWithBookings(item, userId);
            }
        }

        throw new ItemDoesNotExistException("Вещи с id = " + itemId + " не существует.");
    }

    @Override
    public List<ItemDto> getAllItemsDtoByUser(Long userId) {
        checkIfUserExists(userId, userRepository.findAll());
        List<Item> itemsList = itemRepository.findAllByOwner_Id(userId);
        List<ItemDto> itemDtoList = new ArrayList<>();
        for (Item item : itemsList) {
            itemDtoList.add(getItemDtoWithBookings(item, userId));
        }

        itemDtoList.sort((itemDto1, itemDto2) -> {
            if (itemDto1.getId() <= itemDto2.getId()) {
                return -1;
            } else {
                return 1;
            }
        });

        return itemDtoList;
    }

    @Override
    public List<Item> searchItems(String text) {
        List<Item> foundBySearch = new ArrayList<>();

        if (!text.isBlank()) {
            foundBySearch = itemRepository.findAll().stream()
                    .filter(item -> item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                    .filter(item -> item.getAvailable() == true)
                    .collect(Collectors.toList());
        }

        return foundBySearch;
    }

    @Override
    public void checkIfUserExists(Long userId, List<User> users) {
        for (User user : users) {
            if (user.getId().equals(userId)) {
                return;
            }
        }

        throw new UserDoesNotExistException("Пользователя с указанным id не существует.");
    }

    private List<Item> getAllItemsByUser(Long userId) {
        checkIfUserExists(userId, userRepository.findAll());
        return itemRepository.findAllByOwner_Id(userId);
    }

    private Item getItem(Long itemId) {
        List<Item> items = itemRepository.findAll();
        for (Item item : items) {
            if (item.getId() == itemId) {
                return item;
            }
        }

        throw new ItemDoesNotExistException("Вещи с id = " + itemId + " не существует.");
    }

    private boolean isUserOwnerOfItem(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner().getId() == userId) {
            return true;
        }
        return false;
    }

    private ItemDto getItemDtoWithBookings(Item item, Long userId) {
        List<BookingDto> ownersBookings = BookingMapper.mapToBookingDto(
                bookingRepository.getAllBookingsByOwner_IdAndItem_Id(userId, item.getId()));
        List<BookingDto> pastBookings = new ArrayList<>();
        List<BookingDto> futureBookings = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        for (BookingDto bookingDto : ownersBookings) {
            if (bookingDto.getEnd().isBefore(now)) {
                pastBookings.add(bookingDto);
            }
            if (bookingDto.getStart().isAfter(now)) {
                futureBookings.add(bookingDto);
            }
        }
        BookingDto lastBookingDto = null;
        BookingDto nextBookingDto = null;

        if (isUserOwnerOfItem(item.getId(), userId)) {
            if (!pastBookings.isEmpty()) {
                pastBookings.sort((bookingDto1, bookingDto2) -> {
                    if (bookingDto1.getEnd().isBefore(bookingDto2.getEnd())) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
                lastBookingDto = pastBookings.get(pastBookings.size() - 1);
            }
            if (!futureBookings.isEmpty()) {
                futureBookings.sort((bookingDto1, bookingDto2) -> {
                    if (bookingDto1.getStart().isBefore(bookingDto2.getStart())) {
                        return -1;
                    } else {
                        return 1;
                    }
                });
                nextBookingDto = futureBookings.get(0);
            }
        }

        return ItemMapper.mapToItemDtoForOwner(item, lastBookingDto, nextBookingDto);
    }

    public void checkIfUserAndItemExists(Long userId, Long itemId) {
        List<Item> userItems = getAllItemsByUser(userId);
        if (userItems.isEmpty()) {
            throw new UserDoesNotExistOrDoesNotHaveAnyItemsException(
                    "Пользователя с указанным id не существует либо пользователь не добавил ни одной вещи.");
        }
        for (Item item : userItems) {
            if (item.getId() == itemId) {
                return;
            }
        }

        throw new ItemDoesNotExistException("Вещи с указанным id не существует.");
    }

    private void checkIfUserHasRightToPatchOrGetBookings(Long itemId, Long userId) {
        Item item = itemRepository.getReferenceById(itemId);
        if (item.getOwner().getId() != userId) {
            throw new IllegalAccessException("Пользователь с id = " + userId + " не является собственником " +
                    "вещи с id = " + item.getId() + " и не имеет прав ее изменение");
        }
    }
}