package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.dto.ItemForItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestFullDto createItemRequest(ItemRequestCreateDto itemRequestCreateDto, long userId) {
        User requester = checkUserExist(userId);
        itemRequestCreateDto.setCreated(LocalDateTime.now());
        ItemRequest itemRequest = itemRequestMapper.toItemRequestFromCreateDto(itemRequestCreateDto, requester);
        itemRequestRepository.save(itemRequest);
        return itemRequestMapper.toItemRequestFullDto(itemRequest);
    }

    @Override
    public ItemRequestDtoWithItems getItemRequestById(long requestId) {
        ItemRequest itemRequest = checkItemRequestExist(requestId);
        List<ItemForItemRequestDto> items = itemRepository.getItemsByRequestId(requestId).stream()
                .map(itemMapper::toItemForItemRequestDtoFromItem).toList();
        return itemRequestMapper
                .itemRequestDtoWithItemsFromItemRequest(itemRequest, items);
    }

    @Override
    public List<ItemRequestFullDto> getAllItemRequests(long userId) {
        return itemRequestRepository.getItemRequestsAll(userId).stream()
                .sorted((o1, o2) -> o2.getCreated().compareTo(o1.getCreated()))
                .map(itemRequestMapper::toItemRequestFullDto).toList();
    }

    @Override
    public List<ItemRequestFullDto> getItemRequestsByRequesterId(long userId) {
        checkUserExist(userId);
        return itemRequestRepository.getItemRequestsByRequesterId(userId);
    }

    private User checkUserExist(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    }

    private ItemRequest checkItemRequestExist(long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id " + requestId + " not found"));
    }
}
