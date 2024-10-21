package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item,Long> {

    List<Item> getItemsByOwnerId(Long ownerId);

    List<Item> findByNameIgnoreCase(String name);

}
