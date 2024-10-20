package ru.practicum.shareit.user;

import jakarta.validation.ValidationException;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.*;

@Repository
public class InMemUserRepository implements UserRepository {
    private static Long userId = 0L;
    private final Map<Long, User> users = new HashMap<>();
    private final Map<String, Long> emails = new HashMap<>();

    @Override
    public Optional<User> getUserById(Long userId) {
        return Optional.of(users.get(userId));
    }

    @Override
    public User createUser(User user) {
//        user.setId(createUserId());
        users.put(user.getId(), user);
        emails.put(user.getEmail(), user.getId());
        return users.get(user.getId());

    }

    @Override
    public User updateUser(User user) {
        users.replace(user.getId(), user);
        return users.get(user.getId());
    }

    @Override
    public void deleteUser(Long userId) {
        emails.remove(users.get(userId).getEmail());
        users.remove(userId);
    }

    @Override
    public void isUserNotExist(Long userId) {
        if (!users.containsKey(userId)) {
            throw new NotFoundException("User with id - " + userId + " not found");
        }
    }

    @Override
    public void isEmailAlreadyUse(String email) {
        if (emails.containsKey(email)) {
            throw new ValidationException("Email - " + email + " already use");
        }
    }

    private static Long createUserId() {
        return ++userId;
    }
}
