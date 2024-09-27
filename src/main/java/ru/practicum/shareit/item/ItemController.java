package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.Collection;
import java.util.Collections;

@Slf4j
@Validated
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private static final  String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader(SHARER_USER_ID) Long userId,
                              @RequestBody @Valid ItemCreateDto item) {
        log.info("==> Create item: {}", item);
        item.setOwnerId(userId);
        ItemDto itemToCreate = itemService.createItem(item);
        log.info("<== Created item with ID: {}", userId);
        return itemToCreate;
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@PathVariable @NotNull Long itemId,
                              @RequestHeader(SHARER_USER_ID) @NotNull Long userId,
                              @RequestBody @Valid ItemUpdateDto item) {
        log.info("==> Update item with ID: {}", itemId);
        item.setId(itemId);
        item.setOwnerId(userId);
        ItemDto itemToUpdate = itemService.updateItem(item);
        log.info("<== Update item with ID: {}", itemToUpdate.getId());
        return itemToUpdate;
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@PathVariable @NotNull Long itemId) {
        log.info("==> Get item with ID: {}", itemId);
        ItemDto item = itemService.getItemById(itemId);
        log.info("<== Get item with ID: {}", itemId);
        return item;
    }

    @GetMapping
    public Collection<ItemDto> getItemsByUser(@RequestHeader(SHARER_USER_ID) @NotNull Long userId) {
        log.info("==> Get items by user with ID: {}", userId);
        Collection<ItemDto> itemsByUser = itemService.getItemsByUser(userId);
        log.info("<== Get items by user with ID: {}", userId);
        return Collections.unmodifiableCollection(itemsByUser);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByName(@RequestParam("text") @NotNull String name) {
        log.info("==> Search items by query: {}", name);
        Collection<ItemDto> itemsBySearch = itemService.searchByName(name);
        log.info("<== Search items by query: {}", name);
        return Collections.unmodifiableCollection(itemsBySearch);
    }
}
