package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();
    private long idCounter = 1;

    @Override
    public Film create(Film film) {
        film.setId(idCounter++);
        films.put(film.getId(), film);
        log.info("Фильм {} был успешно добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        if (id == 0) {
            log.error("Ошибка при обновлении: Id не может быть равен 0.");
            throw new ConditionsNotMetException("Id не может быть равен 0.");
        }
        if (!films.containsKey(id)) {
            log.error("Ошибка при обновлении, фильм с id {} не найден", id);
            throw new NotFoundException("Фильм с id" + id + "не найден");
        }
        films.put(id, film);
        log.info("Фильм {} успешно обновлён", film.getName());
        return film;
    }

    @Override
    public void delete(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с id" + id + "не найден");
        }
        films.remove(id);
        log.info("Фильм {} успешно удалён.", id);
    }

    @Override
    public Film getById(Long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с ID " + id + " не найден");
        }
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}
