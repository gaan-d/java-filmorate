package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();

    @Override
    public User create(User user) {
        users.put(user.getId(), user);
        log.info("Пользователь {} был успешно добавлен", user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        long id = user.getId();
        users.put(user.getId(), user);
        log.info("Пользователь {} успешно обновлён", user.getName());
        return user;
    }

    @Override
    public void delete(Long id) {
        users.remove(id);
        log.info("Пользователь с id {} успешно удалён.", id);
    }

    @Override
    public User getById(Long id) {
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
