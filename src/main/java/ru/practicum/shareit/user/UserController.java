package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserCreateDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserUpdateDto;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        log.info("==> Request user by id: {}", userId);
        UserDto user = userService.getUserById(userId);
        log.info("<== Get user by id: {}", userId);
        return user;
    }

    @PostMapping
    public UserDto createUser(@RequestBody @Valid final UserCreateDto user) {
        log.info("==> Create user: {}", user);
        UserDto userCreated = userService.createUser(user);
        log.info("<== User created with id : {}", userCreated.getId());
        return userCreated;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable @NotNull Long userId,
                              @RequestBody @Valid final UserUpdateDto user) {
        log.info("==> Update user by id: {}", userId);
        user.setId(userId);
        UserDto userUpdated = userService.updateUser(user);
        log.info("<== User updated by id: {}", userUpdated.getId());
        return userUpdated;
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable @NotNull Long userId) {
        log.info("==> Delete user by id: {}", userId);
        userService.deleteUser(userId);
        log.info("<== User deleted by id: {}", userId);
    }
}
