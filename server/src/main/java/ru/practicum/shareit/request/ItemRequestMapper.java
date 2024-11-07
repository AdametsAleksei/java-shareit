package ru.practicum.shareit.request;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.user.User;

import java.util.List;

@Component
public class ItemRequestMapper {

    public ItemRequest toItemRequestFromCreateDto(ItemRequestCreateDto itemRequest, User requester) {
        return ItemRequest.builder()
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(requester)
                .build();
    }

    public ItemRequestFullDto toItemRequestFullDto(ItemRequest itemRequest) {
        return ItemRequestFullDto.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requester(itemRequest.getRequester())
                .build();
    }

    public ItemRequestDtoWithItems itemRequestDtoWithItemsFromItemRequest(
            ItemRequest itemRequest, List<ItemForItemRequestDto> items) {
        return ItemRequestDtoWithItems.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(itemRequest.getCreated())
                .requesterId(itemRequest.getRequester().getId())
                .items(items)
                .build();
    }
}
