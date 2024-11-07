package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private static final  String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestFullDto createItemRequest(@RequestHeader(SHARER_USER_ID) long userId,
                                                   @RequestBody ItemRequestCreateDto itemRequestCreateDto) {
        log.info("==> Create itemRequest: {}", itemRequestCreateDto);
        ItemRequestFullDto item = itemRequestService.createItemRequest(itemRequestCreateDto, userId);
        log.info("<== Create itemRequest: {}", item);
        return item;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoWithItems getItemRequestById(@RequestHeader(SHARER_USER_ID) long userId,
                                                      @PathVariable("requestId") long requestId) {
        log.info("==> Get itemRequest: {}", requestId);
        ItemRequestDtoWithItems item = itemRequestService.getItemRequestById(requestId);
        log.info("<== Get itemRequest: {}", item);
        return item;
    }

    @GetMapping
    public List<ItemRequestFullDto> getItemRequestsByRequesterId(@RequestHeader(SHARER_USER_ID) long userId) {
        log.info("==> Get itemRequests with RequesterId: {}", userId);
        List<ItemRequestFullDto> itemRequests = itemRequestService.getItemRequestsByRequesterId(userId);
        log.info("<== Get itemRequests with RequesterId: {}", userId);
        return itemRequests;
    }

    @GetMapping("/all")
    public List<ItemRequestFullDto> getItemRequestsAll(@RequestHeader(SHARER_USER_ID) long userId) {
        log.info("==> Get all itemRequests without requesterId: {}", userId);
        List<ItemRequestFullDto> itemRequests = itemRequestService.getAllItemRequests(userId);
        log.info("<== Get all itemRequests without requesterId: {}", userId);
        return itemRequests;
    }

}