package ru.practicum.shareit.request;

import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

import java.util.List;


public interface ItemRequestService {

    ItemRequestFullDto createItemRequest(ItemRequestCreateDto itemRequestDtoFromConsole, long userId);

    ItemRequestDtoWithItems getItemRequestById(long requestId);

    List<ItemRequestFullDto> getAllItemRequests(long userId);

    List<ItemRequestFullDto> getItemRequestsByRequesterId(long userId);

}