package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dal.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public Map<Long, User> getAll() {
        return userStorage.getAll();
    }

    public Collection<User> getAllValues() {
        return userStorage.getAllValues();
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public User create(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        if (user.getId() == 0) {
            log.error("Ошибка при обновлении: Id не может быть равен 0.");
            throw new ConditionsNotMetException("Id не может быть равен 0.");
        }
        return userStorage.update(user);
    }

    public void delete(Long id) {
        userStorage.deleteById(id);
    }

    public void addFriend(@Positive Long userId, @Positive Long friendId) {
        userStorage.addFriend(userId, friendId);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(@Positive Long userId, @Positive Long friendId) {
        userStorage.removeFriend(userId, friendId);
        log.info("Пользователь с id {} удалил из друзей пользователя с id {}", userId, friendId);
    }

    public List<User> getFriends(@Positive Long userId) {
        return userStorage.getAllFriends(userId);
    }

    public List<User> getMutualFriends(@Positive Long userId, @Positive Long friendId) {
        return userStorage.getMutualFriends(userId, friendId);
    }
}
