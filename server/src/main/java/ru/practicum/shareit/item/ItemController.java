package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;

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
                              @RequestBody ItemCreateDto item) {
        log.info("==> Create item: {}", item);
        ItemDto itemToCreate = itemService.createItem(item, userId);
        log.info("<== Created item with ID: {}", userId);
        return itemToCreate;
    }

    @PatchMapping("{itemId}")
    public ItemDto updateItem(@PathVariable Long itemId,
                              @RequestHeader(SHARER_USER_ID) Long userId,
                              @RequestBody ItemUpdateDto item) {
        log.info("==> Update item with ID: {}", itemId);
        item.setId(itemId);
        item.setOwnerId(userId);
        ItemDto itemToUpdate = itemService.updateItem(item);
        log.info("<== Update item with ID: {}", itemToUpdate.getId());
        return itemToUpdate;
    }

    @GetMapping("{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("==> Get item with ID: {}", itemId);
        ItemDto item = itemService.getItemById(itemId, userId);
        log.info("<== Get item with ID: {}", itemId);
        return item;
    }

    @GetMapping
    public Collection<ItemCommentAndDateDto> getItemsByOwner(@RequestHeader(SHARER_USER_ID) Long userId) {
        log.info("==> Get items by user with ID: {}", userId);
        Collection<ItemCommentAndDateDto> itemsByUser = itemService.getItemsByOwnerId(userId);
        log.info("<== Get items by user with ID: {}", userId);
        return Collections.unmodifiableCollection(itemsByUser);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchByName(@RequestParam("text") String name) {
        log.info("==> Search items by query: {}", name);
        Collection<ItemDto> itemsBySearch = itemService.findByName(name);
        log.info("<== Search items by query: {}", name);
        return Collections.unmodifiableCollection(itemsBySearch);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@RequestHeader(SHARER_USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody CommentCreateDto commentCreateDto) {
        log.info("==> Add comment by user ID: {} to item ID: {}", userId, itemId);
        commentCreateDto.setItemId(itemId);
        commentCreateDto.setAuthorId(userId);
        CommentDto comment = itemService.addComment(commentCreateDto);
        log.info("<== Added comment with ID: {}", comment.getId());
        return comment;
    }
}
