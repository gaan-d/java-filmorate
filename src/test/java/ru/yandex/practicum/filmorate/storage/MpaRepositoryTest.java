package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.dal.storage.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({MpaRepository.class})
@ContextConfiguration(classes = {FilmorateApplication.class})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class MpaRepositoryTest {
    MpaRepository mpaDbRepository;

    @Test
    public void getAllMpa() {
        Collection<RatingMpa> mpa = mpaDbRepository.getAllRatings();

        assertEquals(mpa.size(), 5);
    }

    @Test
    public void getMpaById() {
        RatingMpa mpa = mpaDbRepository.getRatingById(1);

        assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
        assertThat(mpa).hasFieldOrPropertyWithValue("name", "G");
    }
}