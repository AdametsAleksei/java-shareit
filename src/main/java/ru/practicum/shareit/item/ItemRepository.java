package ru.practicum.shareit.item;

import java.util.Collection;
import java.util.Optional;

public interface ItemRepository {

    Optional<Item> getItemById(Long itemId);

    Item createItem(Item item);

    Item updateItem(Item item);

    void deleteItem(Item item);

    void isItemNotExist(Long itemId);

    Collection<Item> getItemsByUser(Long userId);

    Collection<Item> searchByName(String name);
}
