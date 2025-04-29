package ru.yandex.practicum.filmorate.dal.storage.film;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.dal.mappers.LikeUserIdRowMapper;
import ru.yandex.practicum.filmorate.dal.storage.BaseRepository;
import ru.yandex.practicum.filmorate.dal.storage.user.UserStorage;
import ru.yandex.practicum.filmorate.dal.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.dal.storage.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.exception.InternalServerException;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;
import java.util.stream.Collectors;

@Repository("filmRepository")
@Primary
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class FilmRepository extends BaseRepository<Film> implements FilmStorage {
    RowMapper<Film> mapper = new FilmRowMapper();
    MpaRepository mpaRepository;
    UserStorage storage;
    GenreRepository genreRepository;

    public FilmRepository(JdbcTemplate jdbc, MpaRepository mpaRepository, UserStorage storage,
                          GenreRepository genreRepository) {
        super(jdbc);
        this.mpaRepository = mpaRepository;
        this.storage = storage;
        this.genreRepository = genreRepository;
    }

    @Override
    public Film create(Film film) {
        RatingMpa mpa = mpaRepository.getRatingById(film.getMpa().getId());

        String query = "INSERT INTO film(name, description, release_date, duration_in_minutes, rating_id)" +
                "VALUES(?, ?, ?, ?, ?)";
        long id = super.create(query,
                film.getName(),
                film.getDescription(),
                Date.valueOf(film.getReleaseDate()),
                film.getDuration(),
                mpa.getId());
        film.setId(id);
        film.setLikes(new HashSet<>());

        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addFilmGenres(film.getId(), film.getGenres());
        }
        film.setMpa(mpa);

        return film;
    }

    @Override
    public Film update(Film film) {
        RatingMpa mpa = mpaRepository.getRatingById(film.getMpa().getId());
        String query = "UPDATE film SET name= ?, description= ?, release_date= ?, duration_in_minutes=?, " +
                "rating_id= ? WHERE id= ?";
        super.update(query,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                mpa.getId(),
                film.getId()
        );

        deleteFilmGenres(film.getId());
        if (film.getGenres() != null && !film.getGenres().isEmpty()) {
            addFilmGenres(film.getId(), film.getGenres());
        }
        if (film.getLikes() != null && !film.getLikes().isEmpty()) {
            for (Long userId : film.getLikes()) {
                addLike(film.getId(), userId);
            }
        }
        return film;
    }

    @Override
    public Film getById(Long id) {
        String query = "SELECT f.*, r.name AS mpa_name FROM film f " +
                "JOIN mpa_rating r ON f.rating_id = r.id " +
                "WHERE f.id = ?";
        Film film = findOne(query, mapper, id)
                .orElseThrow(() -> new NotFoundException(String.format("Фильм с id %d не найден", id)));
        Set<Long> likes = getUserIdsFromLikes(id);
        film.setLikes(likes);
        List<Genre> genres = getGenre(id);
        film.setGenres(genres);
        film.setMpa(mpaRepository.getRatingById(film.getMpa().getId()));
        return film;
    }

    @Override
    public Map<Long, Film> getAll() {
        return getFilms().stream()
                .collect(Collectors.toMap(Film::getId, film -> film));
    }

    @Override
    public List<Film> getFilms() {
        String query = "SELECT * FROM film";
        List<Film> films = findMany(query, mapper);
        for (Film film : films) {
            long id = film.getId();
            film.setLikes(getUserIdsFromLikes(id));
            film.setGenres(getGenre(id));
            film.setMpa(mpaRepository.getRatingById(film.getMpa().getId()));
        }
        return films;
    }

    @Override
    public void delete(Long id) {
        String query = "DELETE FROM film WHERE id = ?";
        boolean isSuccessful = super.delete(query, id);

        if (!isSuccessful) {
            throw new InternalServerException("Не удалось удалить фильм с id: " + id);
        }

        deleteFilmGenres(id);
    }

    @Override
    public Collection<Film> getPopularFilms(int count) {
        String query = """
                    SELECT f.*
                    FROM film f
                    JOIN (
                        SELECT film_id
                        FROM film_like
                        GROUP BY film_id
                        ORDER BY COUNT(user_id) DESC
                        LIMIT ?
                    ) AS top ON f.id = top.film_id;
                """;
        List<Film> popularFilms = findMany(query, mapper, count);
        for (Film film : popularFilms) {
            long id = film.getId();
            film.setLikes(getUserIdsFromLikes(id));
            film.setGenres(getGenre(id));
            film.setMpa(mpaRepository.getRatingById(film.getMpa().getId()));
        }
        return popularFilms;
    }

    private boolean addFilmGenres(Long filmId, List<Genre> genres) {
        String query = "MERGE INTO film_genre " +
                    "KEY (film_id, genre_id) " +
                    "VALUES (?, ?)";
        jdbc.batchUpdate(query, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                Genre genre = genres.get(i);
                genreRepository.getGenreById(genre.getId());
                ps.setLong(1, filmId);
                ps.setLong(2, genre.getId());
            }

            @Override
            public int getBatchSize() {
                return genres.size();
            }
        });
        return true;
    }

    private boolean deleteFilmGenres(Long filmId) {
        String query = "DELETE FROM film_genre WHERE film_id = ?";
        int rowsDeleted = jdbc.update(query, filmId);
        return rowsDeleted > 0;
    }

    @Override
    public void addLike(Long filmId, Long userId) {
        storage.getById(userId);
        getById(filmId);
        String query = "INSERT INTO film_like (film_id, user_id) VALUES (?, ?)";
        super.update(query, filmId, userId);
    }

    @Override
    public void removeLike(Long filmId, Long userId) {
        getById(filmId);
        storage.getById(userId);
        String query = "DELETE FROM film_like WHERE film_id = ? and user_id = ?";
        super.update(query, filmId, userId);
    }

    public Set<Long> getUserIdsFromLikes(long id) {
        String query = "SELECT user_id FROM film_like WHERE film_id = ?";
        return new HashSet<>(jdbc.query(query, new LikeUserIdRowMapper(), id));
    }

    public List<Genre> getGenre(long id) {
        String query = "SELECT g.id, g.name FROM film_genre fg " +
                "INNER JOIN genre g ON fg.genre_id = g.id " +
                "WHERE fg.film_id = ?";
        return new ArrayList<>(jdbc.query(query, new GenreRowMapper(), id));
    }
}
