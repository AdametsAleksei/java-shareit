package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestClient requestClient;
    private static final  String SHARER_USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createItemRequest(@RequestHeader(SHARER_USER_ID) long userId,
                                                    @RequestBody @Valid ItemRequestCreateDto itemRequestCreateDto) {
        return requestClient.createItemRequest(itemRequestCreateDto, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemRequestsByRequesterId(@RequestHeader(SHARER_USER_ID) long userId) {
        return requestClient.getItemRequestByRequesterId(userId);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getItemRequestById(@RequestHeader(SHARER_USER_ID) long userId,
                                                   @PathVariable("requestId") long requestId) {
        return requestClient.getItemRequestById(requestId, userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getItemRequestsAll(@RequestHeader(SHARER_USER_ID) long userId) {
        return requestClient.getItemRequestsAll(userId);
    }
}
