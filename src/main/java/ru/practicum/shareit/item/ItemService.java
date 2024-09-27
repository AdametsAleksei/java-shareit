package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;

public interface ItemService {

    ItemDto getItemById(Long itemId);

    ItemDto createItem(ItemCreateDto item);

    ItemDto updateItem(ItemUpdateDto item);

    Collection<ItemDto> getItemsByUser(Long userId);

    Collection<ItemDto> searchByName(String name);

}
