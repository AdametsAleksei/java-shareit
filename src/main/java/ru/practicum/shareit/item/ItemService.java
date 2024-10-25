package ru.practicum.shareit.item;

import ru.practicum.shareit.item.dto.*;

import java.util.Collection;

public interface ItemService {

    ItemDto getItemById(Long itemId, Long userId);

    ItemDto createItem(ItemCreateDto item, Long userId);

    ItemDto updateItem(ItemUpdateDto item);

    Collection<ItemCommentAndDateDto> getItemsByOwnerId(Long ownerId);

    Collection<ItemDto> findByName(String name);

    CommentDto addComment(CommentCreateDto commentCreateDto);
}
