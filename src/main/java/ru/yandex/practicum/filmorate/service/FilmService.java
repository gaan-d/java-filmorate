package ru.yandex.practicum.filmorate.service;

import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.filmorate.dal.storage.film.FilmStorage;
import ru.yandex.practicum.filmorate.dal.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

@Service
@Slf4j
@Validated
@RequiredArgsConstructor
public class FilmService {
    private static final LocalDate RELEASE_DATE_CHECK = LocalDate.of(1895, 12, 28);
    private final FilmStorage filmStorage;
    private final UserStorage userStorage;


    public void addLike(@Positive Long filmId, @Positive Long userId) {
        filmStorage.addLike(filmId, userId);
        log.info("Пользователь {} поставил лайк фильму {}", userId, filmId);
    }

    public void removeLike(@Positive Long filmId, @Positive Long userId) {
        filmStorage.removeLike(filmId, userId);
        log.info("Пользователь {} удалил лайк с фильма {}", userId, filmId);
    }

    public Film create(Film film) {
        validateReleaseDate(film);
        return filmStorage.create(film);
    }

    public Film update(Film film) {
        if (film.getId() == 0) {
            log.error("Ошибка при обновлении: Id не может быть равен 0.");
            throw new ConditionsNotMetException("Id не может быть равен 0.");
        }
        validateReleaseDate(film);
        return filmStorage.update(film);
    }

    public Map<Long, Film> getAll() {
        return filmStorage.getAll();
    }

    public Film getById(Long id) {
        return filmStorage.getById(id);
    }

    public Collection<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public void delete(Long id) {
        filmStorage.delete(id);
    }

    public Collection<Film> getPopularFilms(int count) {
        return filmStorage.getPopularFilms(count);
    }

    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE_CHECK)) {
            String error = "Дата релиза не должна быть ранее 28.12.1895";
            log.error("Ошибка при обновлении фильма: {}", error);
            throw new ConditionsNotMetException(error);
        }
    }
}
