package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class InMemItemRepository implements ItemRepository {
    private static Long itemId = 0L;
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, Collection<Item>> itemsByUser = new HashMap<>();

    @Override
    public Optional<Item> getItemById(Long itemId) {
        return Optional.of(items.get(itemId));
    }

    @Override
    public Collection<Item> getItemsByUser(Long userId) {
        return itemsByUser.get(userId);
    }

    @Override
    public Collection<Item> searchByName(String name) {
        return items.values().stream().filter(Item::getAvailable)
                .filter(x -> x.getName()
                        .matches("(?i).*" + name + ".*") ||
                        x.getDescription().matches("(?i).*" + name + ".*"))
                .collect(Collectors.toList());
    }

    @Override
    public Item createItem(Item item) {
        item.setId(createItemId());
        items.put(item.getId(), item);
        if (itemsByUser.containsKey(item.getOwnerId())) {
            itemsByUser.get(item.getOwnerId()).add(item);
        } else {
            itemsByUser.put(item.getOwnerId(), new ArrayList<>(List.of(item)));
        }
        return items.get(item.getId());
    }

    @Override
    public Item updateItem(Item item) {
        itemsByUser.get(item.getOwnerId()).remove(items.get(item.getId()));
        itemsByUser.get(item.getOwnerId()).add(item);
        items.replace(item.getId(), item);
        return items.get(item.getId());
    }

    @Override
    public void deleteItem(Item item) {

    }

    @Override
    public void isItemNotExist(Long itemId) {
        if (!items.containsKey(itemId)) {
            throw new NotFoundException("Item with id " + itemId + " not found");
        }
    }

    private static Long createItemId() {
        return ++itemId;
    }
}
