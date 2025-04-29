package ru.yandex.practicum.filmorate.dal.storage.mpa;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.RatingMpaRowMapper;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class MpaRepository {
    RowMapper<RatingMpa> mapper = new RatingMpaRowMapper();
    JdbcTemplate jdbcTemplate;

    public RatingMpa getRatingById(int ratingId) {
        String getByIdQuery = "SELECT * FROM mpa_rating WHERE id = ?";
        Optional<RatingMpa> rating = Optional.ofNullable(jdbcTemplate.queryForObject(getByIdQuery, mapper, ratingId));
        if (rating.isPresent()) {
            return rating.get();
        } else {
            throw new NotFoundException("Рейтинг с таким id не найден:" + ratingId);
        }
    }

    public Collection<RatingMpa> getAllRatings() {
        String getAllQuery = "SELECT * FROM mpa_rating ORDER BY id";
        log.debug("Получаем список рейтингов");
        List<RatingMpa> result = jdbcTemplate.query(getAllQuery, mapper);
        log.trace("Возвращаем все рейтинги: {}.", result);
        return result;
    }
}
