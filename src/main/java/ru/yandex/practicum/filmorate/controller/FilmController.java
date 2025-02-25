package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController extends BaseController<Film> {
    private static final LocalDate RELEASE_DATE_CHECK = LocalDate.of(1895, 12, 28);

    @Override
    public Film create(@Valid @RequestBody Film film) {
        validateReleaseDate(film);
        return super.create(film);
    }

    @Override
    public Film update(@Valid @RequestBody Film film) {
        validateReleaseDate(film);
        return super.update(film);
    }

    // 🔍 Валидация даты релиза
    private void validateReleaseDate(Film film) {
        if (film.getReleaseDate().isBefore(RELEASE_DATE_CHECK)) {
            String error = "Дата релиза не должна быть ранее 28.12.1895";
            log.error("Ошибка при обновлении фильма: {}", error);
            throw new ConditionsNotMetException(error);
        }
    }
}
