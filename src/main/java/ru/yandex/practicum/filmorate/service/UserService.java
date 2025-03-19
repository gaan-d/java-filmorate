package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class UserService {
    private final UserStorage userStorage;
    private long idCounter = 1;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAll() {
        return userStorage.getAll();
    }

    public User getById(Long id) {
        return userStorage.getById(id);
    }

    public User create(User user) {
        user.setId(idCounter++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return userStorage.create(user);
    }

    public User update(User user) {
        long id = user.getId();
        if (id == 0) {
            log.error("Ошибка при обновлении: Id не может быть равен 0.");
            throw new ConditionsNotMetException("Id не может быть равен 0.");
        }
        getByIdOrThrow(id);
        return userStorage.update(user);
    }

    public void delete(Long id) {
        getByIdOrThrow(id);
        userStorage.delete(id);
    }

    public void addFriend(@Positive Long userId, @Positive Long friendId) {
        User user = getByIdOrThrow(userId);
        User friend = getByIdOrThrow(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(@Positive Long userId, @Positive Long friendId) {
        User user = getByIdOrThrow(userId);
        User friend = getByIdOrThrow(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователь {} удалил из друзей пользователя {}", user.getName(), friend.getName());
    }

    public List<User> getFriends(@Positive Long userId) {
        User user = getByIdOrThrow(userId);
        return user.getFriends()
                .stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(@Positive Long userId, @Positive Long friendId) {
        User user = getByIdOrThrow(userId);
        User friend = getByIdOrThrow(friendId);

        List<User> mutualFriends = user.getFriends()
                .stream()
                .filter(friend.getFriends()::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
        log.info("Общие друзья пользователей {} и {}: {}", userId, friendId, mutualFriends);
        return mutualFriends;
    }

    public User getByIdOrThrow(Long id) {
        User user = userStorage.getById(id);
        if (user == null) {
            log.error("Ошибка: пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return user;
    }
}
