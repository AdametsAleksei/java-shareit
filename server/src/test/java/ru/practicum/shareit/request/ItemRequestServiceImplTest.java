package ru.practicum.shareit.request;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestCreateDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoWithItems;
import ru.practicum.shareit.request.dto.ItemRequestFullDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {

    private final ItemRequestService itemRequestService;

    private final UserService userService;
    private final ItemService itemService;

    ItemRequestCreateDto itemRequestCreateDtoCreateDto;
    String description = "OK";


    UserCreateDto user = new UserCreateDto();
    Long userId;

    @BeforeEach
    void setUp() {
        user.setName("NAME");
        user.setEmail("ya@ya.ru");
        userId = userService.saveUser(user).getId();
        itemRequestCreateDtoCreateDto = new ItemRequestCreateDto();
        itemRequestCreateDtoCreateDto.setDescription(description);
    }

    @Test
    void createItemRequest_WithWrongUser_ShouldBeException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, 10));
    }

    @Test
    void createItemRequest() {
        ItemRequestFullDto item = itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        assertEquals(itemRequestCreateDtoCreateDto.getDescription(), item.getDescription());
    }

    @Test
    void getItemRequestById_WithWrongId_ShouldBeException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestById(10));
    }

    @Test
    void getItemRequestById() {
        ItemRequestFullDto item = itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        assertEquals(itemRequestCreateDtoCreateDto.getDescription(),
                itemRequestService.getItemRequestById(item.getId()).getDescription());
    }

    @Test
    void getItemRequestsByRequesterId_WithWrongUserId_ShouldBeException() {
        Assertions.assertThrows(NotFoundException.class,
                () -> itemRequestService.getItemRequestsByRequesterId(10));
    }

    @Test
    void getItemRequestsByRequesterId() {
        ItemRequestFullDto item = itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        assertEquals(item.getId(), itemRequestService.getItemRequestsByRequesterId(userId).getFirst().getId());
        assertEquals(1,  itemRequestService.getItemRequestsByRequesterId(userId).size());
    }

    @Test
    void getItemRequestsAll() {
        List<ItemRequestFullDto> list = itemRequestService.getAllItemRequests(userId);
        assertEquals(0, list.size());
    }

    @Test
    void getAllItemRequests_WithSeveralRequests_ShouldReturnSorted() {
        ItemRequestFullDto item1 = itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        itemRequestCreateDtoCreateDto.setDescription("Another Request");
        ItemRequestFullDto item2 = itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        List<ItemRequestFullDto> requests = itemRequestService.getAllItemRequests(2);
        assertEquals(2, requests.size());
        assertEquals(item2.getDescription(), requests.get(0).getDescription());
        assertEquals(item1.getDescription(), requests.get(1).getDescription());
    }

    @Test
    void getItemRequestsByRequesterId_WithSeveralRequests_ShouldReturnAllRequests() {
        itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        itemRequestCreateDtoCreateDto.setDescription("Another Request");
        itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        List<ItemRequestFullDto> requests = itemRequestService.getItemRequestsByRequesterId(userId);
        assertEquals(2, requests.size());
    }

    @Test
    void getItemRequestById_WithItems_ShouldReturnItems() {
        ItemRequestFullDto itemRequest = itemRequestService.createItemRequest(itemRequestCreateDtoCreateDto, userId);
        ItemCreateDto itemCreate = new ItemCreateDto();
        itemCreate.setOwnerId(userId);
        itemCreate.setName("Item");
        itemCreate.setAvailable(true);
        itemCreate.setDescription("Description");
        itemCreate.setRequestId(itemRequest.getId());
        Long itemId = itemService.createItem(itemCreate, userId).getId();

        ItemRequestDtoWithItems requestWithItems = itemRequestService.getItemRequestById(itemRequest.getId());
        assertEquals(1, requestWithItems.getItems().size());
        assertEquals(itemId, requestWithItems.getItems().getFirst().getId());
    }
}