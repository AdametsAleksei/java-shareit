package ru.practicum.shareit.item;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemUpdateDto;
import ru.practicum.shareit.user.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

@Service
@AllArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto getItemById(Long itemId) {
        userRepository.isUserNotExist(itemId);
        return itemMapper.toItemDto(itemRepository.getItemById(itemId).get());
    }

    @Override
    public Collection<ItemDto> getItemsByUser(Long userId) {
        userRepository.isUserNotExist(userId);
        Collection<ItemDto> itemsByUser = new ArrayList<>();
        itemRepository.getItemsByUser(userId).stream()
                .peek(itemMapper::toItemDto)
                .forEach(item -> itemsByUser.add(itemMapper.toItemDto(item)));
        return itemsByUser;
    }

    @Override
    public ItemDto createItem(ItemCreateDto item) {
        userRepository.isUserNotExist(item.getOwnerId());
        return itemMapper.toItemDto(itemRepository.createItem(itemMapper.toItemFromCreateDto(item)));
    }

    @Override
    public ItemDto updateItem(ItemUpdateDto item) {
        itemRepository.isItemNotExist(item.getId());
        userRepository.isUserNotExist(item.getOwnerId());
        if (Objects.isNull(item.getName())) {
            item.setName(getItemById(item.getId()).getName());
        }
        if (Objects.isNull(item.getDescription())) {
            item.setDescription(getItemById(item.getId()).getDescription());
        }
        if (Objects.isNull(item.getAvailable())) {
            item.setAvailable(getItemById(item.getId()).getAvailable());
        }
        return itemMapper.toItemDto(itemRepository.updateItem(itemMapper.toItemFromUpdateDto(item)));
    }

    @Override
    public Collection<ItemDto> searchByName(String name) {
        if (name.isEmpty()) {
            return new ArrayList<>();
        }
        Collection<ItemDto> itemsBySearch = new ArrayList<>();
        itemRepository.searchByName(name).stream()
                .peek(itemMapper::toItemDto)
                .forEach(item -> itemsBySearch.add(itemMapper.toItemDto(item)));
        return itemsBySearch;
    }
}
