package ru.yandex.practicum.filmorate.dal.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;
import java.util.Map;

public interface UserStorage {
    User create(User user);

    List<User> getAllValues();

    User update(User user);

    User getById(Long id);

    Map<Long, User> getAll();

    void deleteById(Long id);

    void addFriend(Long userId, Long friendId);

    void removeFriend(Long userId, Long friendId);

    List<User> getMutualFriends(Long userId1, Long userId2);

    List<User> getAllFriends(Long userId);
}
