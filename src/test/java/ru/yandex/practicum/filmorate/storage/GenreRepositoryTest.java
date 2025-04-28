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
import ru.yandex.practicum.filmorate.dal.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.Collection;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({GenreRepository.class})
@ContextConfiguration(classes = {FilmorateApplication.class})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class GenreRepositoryTest {
    GenreRepository genreDbRepository;

    @Test
    public void getAllGenres() {
        Collection<Genre> genres = genreDbRepository.getAllGenres();

        assertEquals(genres.size(), 6);
    }

    @Test
    public void getGenreById() {
        Optional<Genre> genreOptional = genreDbRepository.getGenreById(1);

        assertThat(genreOptional)
                .isPresent()
                .hasValueSatisfying(mpa -> {
                    assertThat(mpa).hasFieldOrPropertyWithValue("id", 1);
                    assertThat(mpa).hasFieldOrPropertyWithValue("name", "Комедия");
                });
    }
}