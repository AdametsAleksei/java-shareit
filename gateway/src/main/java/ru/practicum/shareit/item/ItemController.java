package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentCreateDto;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;

import java.util.List;

@Controller
@RequestMapping(path = "/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private static final  String SHARER_USER_ID = "X-Sharer-User-Id";
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(SHARER_USER_ID) Long userId,
                              @RequestBody @Valid ItemCreateDto item) {
        return itemClient.createItem(userId, item);
    }

    @PatchMapping("{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable @NotNull Long itemId,
                              @RequestHeader(SHARER_USER_ID) @NotNull Long userId,
                              @RequestBody @Valid ItemUpdateDto item) {
        return itemClient.updateItem(userId, item, itemId);
    }

    @GetMapping("{itemId}")
    public ResponseEntity<Object> getItemById(@PathVariable @NotNull Long itemId,
                               @RequestHeader(SHARER_USER_ID) @NotNull Long userId) {
        return itemClient.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUser(@RequestHeader(SHARER_USER_ID) @NotNull Long userId) {
        return itemClient.getItemsByUsers(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchByName(@RequestParam("text") @NotNull String name,
                                               @RequestHeader(SHARER_USER_ID) @NotNull Long userId) {
        if (name.isEmpty()) {
            return ResponseEntity.ok(List.of());
        } else {
            return itemClient.searchByName(userId, name);
        }
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader(SHARER_USER_ID) Long userId,
                                 @PathVariable Long itemId,
                                 @RequestBody @Valid CommentCreateDto commentCreateDto) {
        return itemClient.addComment(userId, itemId, commentCreateDto);
    }
}