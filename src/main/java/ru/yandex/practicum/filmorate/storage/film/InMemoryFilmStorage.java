package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class InMemoryFilmStorage implements FilmStorage {
    private final Map<Long, Film> films = new HashMap<>();


    @Override
    public Film create(Film film) {
        films.put(film.getId(), film);
        log.info("Фильм {} был успешно добавлен", film.getName());
        return film;
    }

    @Override
    public Film update(Film film) {
        long id = film.getId();
        films.put(id, film);
        log.info("Фильм {} успешно обновлён", film.getName());
        return film;
    }

    @Override
    public void delete(Long id) {
        films.remove(id);
        log.info("Фильм {} успешно удалён.", id);
    }

    @Override
    public Film getById(Long id) {
        return films.get(id);
    }

    @Override
    public List<Film> getAll() {
        return new ArrayList<>(films.values());
    }
}
