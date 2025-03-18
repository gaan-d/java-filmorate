package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class UserService {
    private final UserStorage userStorage;

    @Autowired
    public UserService(UserStorage userStorage) {
        this.userStorage = userStorage;
    }

    public List<User> getAll(){
        return  userStorage.getAll();
    }

    public User getById(Long id){
        return userStorage.getById(id);
    }

    public User create(User user){
        return userStorage.create(user);
    }

    public User update(User user){
        return userStorage.update(user);
    }

    public void delete(Long id){
        userStorage.delete(id);
    }

    public void addFriend(@Positive Long userId, @Positive Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().add(friendId);
        friend.getFriends().add(userId);

        userStorage.update(user);
        userStorage.update(friend);
        log.info("Пользователь {} добавил в друзья пользователя {}", userId, friendId);
    }

    public void removeFriend(@Positive Long userId, @Positive Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);

        userStorage.update(user);
        userStorage.update(friend);

        log.info("Пользователь {} удалил из друзей пользователя {}", user.getName(), friend.getName());
    }

    public List<User> getFriends(@Positive Long userId) {
        User user = getById(userId);
        return user.getFriends()
                .stream()
                .map(userStorage::getById)
                .collect(Collectors.toList());
    }

    public List<User> getMutualFriends(@Positive Long userId, @Positive Long friendId) {
        User user = getById(userId);
        User friend = getById(friendId);

        List<User> mutualFriends = user.getFriends()
                .stream()
                .filter(friend.getFriends()::contains)
                .map(userStorage::getById)
                .collect(Collectors.toList());
        log.info("Общие друзья пользователей {} и {}: {}", userId, friendId, mutualFriends);
        return mutualFriends;
    }
}
