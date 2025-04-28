package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreService {
    GenreRepository genreRepository;

    public Collection<Genre> getAllGenres() {
        return genreRepository.getAllGenres();
    }

    public Genre getGenreById(int id) {
        return genreRepository.getGenreById(id)
                .orElseThrow(() -> new NotFoundException("Жанр с id " + id + " не найден."));
    }
}
