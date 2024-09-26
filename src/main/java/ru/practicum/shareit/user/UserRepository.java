package ru.practicum.shareit.user;

import java.util.Optional;

public interface UserRepository {

    Optional<User> getUserById(Long userId);

    User createUser(User user);

    User updateUser(User user);

    void deleteUser(Long userId);

    void isUserNotExist(Long userId);

    void isEmailAlreadyUse(String email);

}
