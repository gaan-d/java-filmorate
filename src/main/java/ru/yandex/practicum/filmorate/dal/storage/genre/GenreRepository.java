package ru.yandex.practicum.filmorate.dal.storage.genre;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class GenreRepository {
    RowMapper<Genre> rowMapper = new GenreRowMapper();
    JdbcTemplate jdbcTemplate;

    public Optional<Genre> getGenreById(int id) {
        String getById = "SELECT * FROM genre WHERE id =?";
        Optional<Genre> genre = Optional.ofNullable(jdbcTemplate.queryForObject(getById, rowMapper, id));
        return genre;
    }

    public Collection<Genre> getAllGenres() {
        String getAll = "SELECT * FROM genre ORDER BY id";
        log.debug("Получаем все жанры");
        List<Genre> genres = jdbcTemplate.query(getAll, rowMapper);
        log.trace("Возвращаем жанры: {}.", genres);
        return genres;
    }
}
