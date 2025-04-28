package ru.yandex.practicum.filmorate.dal.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface FilmStorage {
    Film create(Film film);

    Film update(Film film);

    Film getById(Long id);

    Map<Long, Film> getAll();

    List<Film> getFilms();

    void delete(Long id);

    Collection<Film> getPopularFilms(int count);

    void addLike(Long filmId, Long userId);

    void removeLike(Long filmId, Long userId);
}
