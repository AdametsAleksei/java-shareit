package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserFullDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserFullDto getUserById(@PathVariable Long userId) {
        log.info("==> Request user by id: {}", userId);
        UserFullDto user = userService.getUserById(userId);
        log.info("<== Get user by id: {}", userId);
        return user;
    }

    @PostMapping
    public UserFullDto createUser(@RequestBody final UserCreateDto user) {
        log.info("==> Create user: {}", user);
        UserFullDto userCreated = userService.saveUser(user);
        log.info("<== User created with id : {}", userCreated.getId());
        return userCreated;
    }

    @PatchMapping("/{userId}")
    public UserFullDto updateUser(@PathVariable Long userId,
                                  @RequestBody final UserUpdateDto user) {
        log.info("==> Update user by id: {}", userId);
        user.setId(userId);
        UserFullDto userUpdated = userService.updateUser(user);
        log.info("<== User updated by id: {}", userUpdated.getId());
        return userUpdated;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        log.info("==> Delete user by id: {}", userId);
        userService.deleteUser(userId);
        log.info("<== User deleted by id: {}", userId);
    }
}
