package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

@Component
public class ItemMapper {

    public Item toItemFromCreateDto(ItemCreateDto item, User owner, ItemRequest itemRequest) {
        return Item.builder()
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .request(itemRequest)
                .build();
    }

    public ItemDto toItemFullDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .ownerId(item.getOwner().getId())
                .itemRequest(item.getRequest())
                .build();
    }

    public Item toItemFromUpdateDto(ItemUpdateDto item, User owner) {
        return Item.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .owner(owner)
                .build();
    }

    public ItemCommentAndDateDto toItemCommentAndDateDtoFromItem(Item item) {
        return ItemCommentAndDateDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
    }

    public ItemForItemRequestDto toItemForItemRequestDtoFromItem(Item item) {
        return ItemForItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .ownerId(item.getOwner().getId())
                .build();
    }
}
