package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@Validated
public class FilmService {
    private static final LocalDate RELEASE_DATE_CHECK = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserStorage userStorage) {
        this.filmStorage = filmStorage;
        this.userStorage = userStorage;
    }

    public void addLike(@Positive Long filmId, @Positive Long userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);

        if (!film.getLikes().contains(userId)) {
            film.getLikes().add(userId);
            filmStorage.update(film);
            log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
        }
    }

    public void removeLike(@Positive Long filmId, @Positive Long userId) {
        Film film = filmStorage.getById(filmId);
        userStorage.getById(userId);

        if (!film.getLikes().contains(userId)) {
            throw new ConditionsNotMetException("Пользователь с ID " + userId + " не ставил лайк этому фильму");
        }
        film.getLikes().remove(userId);
        filmStorage.update(film);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    public List<Film> getPopularFilms(@Min(1) int count) {
        return filmStorage.getAll().stream()
                .sorted((film1, film2) -> Integer.compare(film2.getLikes().size(), film1.getLikes().size()))
                .limit(count)
                .collect(Collectors.toList());
    }

    public Film create(Film film) {
        validateReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        validateReleaseDate(film);
        return filmStorage.update(film);
    }

    public List<Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id);
    }

    public void delete(Long id) {
        filmStorage.delete(id);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE_CHECK)) {
            String error = "Дата релиза не должна быть ранее 28.12.1895";
            log.error("Ошибка при обновлении фильма: {}", error);
            throw new ConditionsNotMetException(error);
        }
    }
}
