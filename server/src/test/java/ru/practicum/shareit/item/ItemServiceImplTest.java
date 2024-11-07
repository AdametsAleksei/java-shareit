package ru.practicum.shareit.item;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingCreateDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserCreateDto;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {

    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;

    ItemCreateDto itemCreateDto;
    CommentCreateDto commentCreateDto;
    UserCreateDto userCreateDto;
    ItemUpdateDto updateDto;
    Long userId;

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setName("Aleksei");
        userCreateDto.setEmail("aleksei@gmail.com");
        userId = userService.saveUser(userCreateDto).getId();

        itemCreateDto = new ItemCreateDto();
        itemCreateDto.setName("ITEM_1");
        itemCreateDto.setDescription("Description");
        itemCreateDto.setAvailable(true);

        updateDto = new ItemUpdateDto();
        updateDto.setName("NEW_NAME");
        updateDto.setDescription("NEW_DESCRIPTION");
        updateDto.setAvailable(false);

        commentCreateDto = CommentCreateDto.builder().build();
    }

    @Test
    void createItem_WithWrongUser_ShouldBeException() {
        Assertions.assertThrows(NotFoundException.class, () -> itemService.createItem(itemCreateDto, 1000L));
    }

    @Test
    void createItem() {
        ItemDto itemDone = itemService.createItem(itemCreateDto, userId);
        assertEquals(itemCreateDto.getName(), itemDone.getName());
        assertEquals(itemCreateDto.getAvailable(), itemDone.getAvailable());
        assertEquals(itemCreateDto.getDescription(), itemDone.getDescription());
    }

    @Test
    void updateItem_WrongItemId_ShouldBeException() {
        updateDto.setId(100L);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(updateDto));
    }

    @Test
    void updateItem_WrongOwnerId_ShouldBeException() {
        Long id = itemService.createItem(itemCreateDto, userId).getId();
        updateDto.setId(id);
        updateDto.setOwnerId(1000L);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.updateItem(updateDto));
    }

    @Test
    void updateItem_ShouldBeUpdatedNameAndDescriptionAndAvailable() {
        Long id = itemService.createItem(itemCreateDto, userId).getId();
        updateDto.setId(id);
        updateDto.setOwnerId(userId);
        ItemDto itemDone = itemService.updateItem(updateDto);
        assertEquals(itemDone.getOwnerId(), userId);
        assertEquals(updateDto.getName(), itemDone.getName());
        assertEquals(updateDto.getDescription(), itemDone.getDescription());
        assertEquals(updateDto.getAvailable(), itemDone.getAvailable());
    }

    @Test
    void getItemById_WrongItemId_ShouldBeException() {
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemById(100L, userId));
    }

    @Test
    void getItemById() {
        ItemDto itemDone = itemService.createItem(itemCreateDto, userId);
        assertEquals(itemDone.getName(), itemService.getItemById(itemDone.getId(), userId).getName());
        assertEquals(itemDone.getDescription(), itemService.getItemById(itemDone.getId(), userId).getDescription());
        assertEquals(itemDone.getAvailable(), itemService.getItemById(itemDone.getId(), userId).getAvailable());
    }

    @Test
    void getItemsByOwner_WrongOwnerId_ShouldBeException() {
        Assertions.assertThrows(NotFoundException.class, () -> itemService.getItemsByOwnerId(1000L));
    }

    @Test
    void getItemsByOwner() {
        itemService.createItem(itemCreateDto, userId);
        List<ItemCommentAndDateDto> items = itemService.getItemsByOwnerId(userId).stream().toList();
        assertEquals(items.size(), 1);
        assertEquals(items.getFirst().getName(), itemCreateDto.getName());
    }

    @Test
    void findByName_EmptyName_ShouldBeEqualsNull() {
        itemService.createItem(itemCreateDto, userId);
        List<ItemDto> items = itemService.findByName("").stream().toList();
        assertEquals(items.size(), 0);
    }

    @Test
    void findByName() {
        itemService.createItem(itemCreateDto, userId);
        List<ItemDto> items2 = itemService.findByName("ITEM_1").stream().toList();
        assertEquals(items2.getFirst().getName(), itemCreateDto.getName());
    }

    @Test
    void addComment_UserNotExit_ShouldBeException() {
        commentCreateDto.setAuthorId(100L);
        Assertions.assertThrows(NotFoundException.class, () -> itemService.addComment(commentCreateDto));
    }

    @Test
    void addComment_UserNotBooked_ShouldBeException() {
        Long id = itemService.createItem(itemCreateDto, userId).getId();
        commentCreateDto.setAuthorId(userId);
        commentCreateDto.setItemId(id);
        Assertions.assertThrows(ValidationException.class, () -> itemService.addComment(commentCreateDto));
    }

    @Test
    void addComment_UserWithoutBooking_ShouldBeException() {
        UserCreateDto userNew = new UserCreateDto();
        userNew.setName("AA");
        userNew.setEmail("new@ya.ru");

        Long idBooker = userService.saveUser(userNew).getId();
        Long idItem = itemService.createItem(itemCreateDto, userId).getId();
        commentCreateDto.setAuthorId(idBooker);
        commentCreateDto.setItemId(idItem);
        commentCreateDto.setText("OK");
        Assertions.assertThrows(ValidationException.class, () -> itemService.addComment(commentCreateDto));
    }

    @Test
    void addComment() {
        UserCreateDto userNew = new UserCreateDto();
        userNew.setName("AA");
        userNew.setEmail("new@ya.ru");

        Long idBooker = userService.saveUser(userNew).getId();
        Long idItem = itemService.createItem(itemCreateDto, userId).getId();
        BookingCreateDto bookingCreateDto = new BookingCreateDto();
        bookingCreateDto.setItemId(idItem);
        bookingCreateDto.setStart(LocalDateTime.now().minusHours(5));
        bookingCreateDto.setEnd(LocalDateTime.now().minusHours(1));
        bookingService.addBooking(bookingCreateDto, idBooker);
        commentCreateDto.setAuthorId(idBooker);
        commentCreateDto.setItemId(idItem);
        commentCreateDto.setText("OK");
        assertEquals(commentCreateDto.getText(), itemService.addComment(commentCreateDto).getText());
    }
}