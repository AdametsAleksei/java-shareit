package ru.practicum.shareit.user;

import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    private final UserService userService;

    UserCreateDto userCreateDto;
    UserCreateDto userCreateDto2;
    UserFullDto userDone;
    UserUpdateDto userUpdateDto;
    Long userId = 1L;
    String name = "Aleksei";
    String email = "aleksei@ya.ru";

    @BeforeEach
    void setUp() {
        userCreateDto = new UserCreateDto();
        userCreateDto.setName(name);
        userCreateDto.setEmail(email);

        userCreateDto2 = new UserCreateDto();
        userCreateDto2.setName(name);
        userCreateDto2.setEmail("wrong@ya.ru");

        userUpdateDto = new UserUpdateDto();
        userUpdateDto.setId(userId);
        userUpdateDto.setName("Aleksei 2.0");
        userUpdateDto.setEmail("new@ya.ru");

        userDone = UserFullDto.builder()
                .id(userId)
                .name(name)
                .email(email)
                .build();
    }

    @Test
    void saveUser_shouldSaveUser() {
        UserFullDto userDone = userService.saveUser(userCreateDto2);
        assertEquals(userCreateDto2.getEmail(), userDone.getEmail());
        assertEquals(userCreateDto2.getName(), userDone.getName());
    }

    @Test
    void updateUser_WithWrongId_ShouldReturnException() {
        userUpdateDto.setId(5L);
        Assertions.assertThrows(NotFoundException.class, () -> userService.updateUser(userUpdateDto));
        userUpdateDto.setId(1L);
    }

    @Test
    void updateUser_WithExistEmail_ShouldReturnException() {
        userService.saveUser(userCreateDto2);
        userUpdateDto.setEmail("wrong@ya.ru");
        Assertions.assertThrows(ValidationException.class, () -> userService.updateUser(userUpdateDto));
    }

    @Test
    void updateUser_UserNameEmailShouldBeUpdated() {
        userUpdateDto.setId(userService.saveUser(userCreateDto).getId());
        userService.updateUser(userUpdateDto);
        assertEquals("Aleksei 2.0", userUpdateDto.getName());
        assertEquals("new@ya.ru", userUpdateDto.getEmail());
    }

    @Test
    void getUserById_ShouldReturnUser() {
        UserFullDto user = userService.getUserById(userService.saveUser(userCreateDto).getId());
        assertEquals(user.getName(), userCreateDto.getName());
        assertEquals(user.getEmail(), userCreateDto.getEmail());
    }

    @Test
    void getUserById_IdNotExist_ShouldReturnException() {
        Assertions.assertThrows(NotFoundException.class, () -> userService.getUserById(1000L));
    }

    @Test
    void deleteUserById_ShouldDeleteUser() {
        userService.deleteUser(userId);
        Assertions.assertThrows(
                NotFoundException.class, () -> userService.getUserById(userId));
    }
}
