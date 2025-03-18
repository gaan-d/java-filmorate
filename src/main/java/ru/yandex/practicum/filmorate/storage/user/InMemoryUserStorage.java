package ru.yandex.practicum.filmorate.storage.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users = new HashMap<>();
    private long idCounter = 1;

    @Override
    public User create(User user) {
        user.setId(idCounter++);
        if (user.getName() == null || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        users.put(user.getId(), user);
        log.info("Пользователь {} был успешно добавлен", user.getName());
        return user;
    }

    @Override
    public User update(User user) {
        long id = user.getId();
        if (id == 0) {
            log.error("Ошибка при обновлении: Id не может быть равен 0.");
            throw new ConditionsNotMetException("Id не может быть равен 0.");
        }
        if (!users.containsKey(id)) {
            log.error("Ошибка при обновлении, пользователь с id {} не найден", id);
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }

        users.put(id, user);
        log.info("Пользователь {} успешно обновлён", user.getName());
        return user;
    }

    @Override
    public void delete(Long id) {
        log.info("Поиск пользователя с id: {}", id);
        log.info("Все сохранённые ID: {}", users.keySet());
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        users.remove(id);
        log.info("Пользователь с id {} успешно удалён.", id);
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)){
           throw new NotFoundException("Пользователь с id " + id + " не найден");
        }
        return users.get(id);
    }

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }
}
