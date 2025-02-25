package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.BaseEntity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public abstract class BaseController<T extends BaseEntity> {
    private long idCounter = 0;
    protected final Map<Long, T> storage = new HashMap<>();

    @GetMapping
    public Collection<T> getAll() {
        return storage.values();
    }

    @PostMapping
    public T create(@Valid @RequestBody T entity) {
        entity.setId(++idCounter);
        storage.put(entity.getId(), entity);
        log.info("{} успешно создан: {}", entity.getClass().getSimpleName(), entity);
        return entity;
    }

    @PutMapping
    public T update(@Valid @RequestBody T entity) {
        long id = entity.getId();
        if (id == 0) {
            log.error("Ошибка при обновлении: ID не может быть равен 0");
            throw new ConditionsNotMetException("ID не может быть равен 0");
        }
        if (!storage.containsKey(id)) {
            log.error("Ошибка при обновлении: Сущность с ID {} не найдена", id);
            throw new NotFoundException("Сущность с ID " + id + " не найдена");
        }
        storage.put(id, entity);
        log.info("{} успешно обновлена: {}", entity.getClass().getSimpleName(), entity);
        return entity;
    }
}
