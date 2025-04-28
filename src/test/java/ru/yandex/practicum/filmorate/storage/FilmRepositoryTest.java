package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.filmorate.FilmorateApplication;
import ru.yandex.practicum.filmorate.dal.storage.film.FilmRepository;
import ru.yandex.practicum.filmorate.dal.storage.genre.GenreRepository;
import ru.yandex.practicum.filmorate.dal.storage.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.dal.storage.user.UserRepository;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@JdbcTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Import({FilmRepository.class, UserRepository.class, MpaRepository.class, GenreRepository.class})
@ContextConfiguration(classes = {FilmorateApplication.class})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class FilmRepositoryTest {
    FilmRepository filmDbRepository;
    UserRepository userDbRepository;
    MpaRepository mpaDbRepository;

    @Test
    void createTest() {
        Film film = createFilm();
        Film newFilm = filmDbRepository.create(film);

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", newFilm.getId());
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", film.getName());
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", film.getDescription());
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", film.getReleaseDate());
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", film.getDuration());
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", film.getMpa());

        assertThat(newFilm).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newFilm).hasFieldOrPropertyWithValue("name", "name");
        assertThat(newFilm).hasFieldOrPropertyWithValue("description", "description");
        assertThat(newFilm).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 9, 9));
        assertThat(newFilm).hasFieldOrPropertyWithValue("duration", 140);
        assertThat(newFilm).hasFieldOrPropertyWithValue("mpa", mpaDbRepository.getRatingById(3));
    }

    private Film createFilm() {
        return Film
                .builder()
                .name("name")
                .description("description")
                .releaseDate(LocalDate.of(1999, 9, 9))
                .duration(140)
                .mpa(mpaDbRepository.getRatingById(3))
                .build();
    }

    @Test
    void getByIdTest() {
        Film film = createFilm();
        Film newFilm = filmDbRepository.create(film);
        Film filmById = filmDbRepository.getById(newFilm.getId());

        assertThat(filmById).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(filmById).hasFieldOrPropertyWithValue("name", "name");
        assertThat(filmById).hasFieldOrPropertyWithValue("description", "description");
        assertThat(filmById).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 9, 9));
        assertThat(filmById).hasFieldOrPropertyWithValue("duration", 140);
        assertThat(filmById).hasFieldOrPropertyWithValue("mpa", mpaDbRepository.getRatingById(3));
    }

    @Test
    void getAllTest() {
        Film film1 = filmDbRepository.create(createFilm());
        Film film2 = filmDbRepository.create(createFilm());
        Map<Long, Film> collection = filmDbRepository.getAll();

        assertEquals(collection.size(), 2, "Неверное количество фильмов");
        assertEquals(collection.get(1L), film1, "Ошибка при возвращении film1");
        assertEquals(collection.get(2L), film2, "Ошибка при возвращении film2");
    }

    @Test
    void getFilmsTest() {
        Film film1 = filmDbRepository.create(createFilm());
        Film film2 = filmDbRepository.create(createFilm());
        List<Film> collection = filmDbRepository.getFilms();

        assertEquals(collection.size(), 2, "Неверное количество фильмов");
        assertEquals(collection.get(0), film1, "Ошибка при возвращении film1");
        assertEquals(collection.get(1), film2, "Ошибка при возвращении film2");
    }

    @Test
    void updateTest() {
        Film film = filmDbRepository.create(createFilm());
        Film filmUpdate = Film.builder()
                .id(film.getId())
                .name("name2")
                .description("description2")
                .releaseDate(LocalDate.of(1999, 4, 9))
                .duration(100)
                .mpa(mpaDbRepository.getRatingById(3))
                .build();
        filmDbRepository.update(filmUpdate);
        film = filmDbRepository.getById(1L);

        assertThat(film).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(film).hasFieldOrPropertyWithValue("name", "name2");
        assertThat(film).hasFieldOrPropertyWithValue("description", "description2");
        assertThat(film).hasFieldOrPropertyWithValue("releaseDate", LocalDate.of(1999, 4, 9));
        assertThat(film).hasFieldOrPropertyWithValue("duration", 100);
        assertThat(film).hasFieldOrPropertyWithValue("mpa", mpaDbRepository.getRatingById(3));
    }

    @Test
    void deleteByIdTest() {
        Film film1 = filmDbRepository.create(createFilm());
        Film film2 = filmDbRepository.create(createFilm());
        filmDbRepository.delete(1L);
        Map<Long, Film> collection = filmDbRepository.getAll();

        assertEquals(collection.size(), 1, "Неверное количество фильмов");
        assertEquals(collection.get(2L), film2, "Ошибка при возвращении film2");
    }

    @Test
    void addLikeTest() {
        User user1 = User.builder()
                .login("SomeLogin")
                .name("SomeName")
                .email("test@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        user1 = userDbRepository.create(user1);
        User user2 = User.builder()
                .login("SomeLogin2")
                .name("SomeName2")
                .email("test2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        user2 = userDbRepository.create(user2);

        Film film = filmDbRepository.create(createFilm());
        filmDbRepository.addLike(film.getId(), user1.getId());
        filmDbRepository.addLike(film.getId(), user2.getId());

        Set<Long> likes = filmDbRepository.getUserIdsFromLikes(film.getId());

        assertEquals(likes.size(), 2, "Количество пользователей возвращается неверно");
        assertTrue(likes.contains(user1.getId()));
        assertTrue(likes.contains(user2.getId()));
    }

    @Test
    void deleteLikeTest() {
        User user1 = User.builder()
                .login("SomeLogin")
                .name("SomeName")
                .email("test@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        user1 = userDbRepository.create(user1);
        User user2 = User.builder()
                .login("SomeLogin2")
                .name("SomeName2")
                .email("test2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        user2 = userDbRepository.create(user2);

        Film film = filmDbRepository.create(createFilm());
        filmDbRepository.addLike(film.getId(), user1.getId());
        filmDbRepository.addLike(film.getId(), user2.getId());

        filmDbRepository.removeLike(film.getId(), user1.getId());

        Set<Long> likes = filmDbRepository.getUserIdsFromLikes(film.getId());

        assertEquals(likes.size(), 1, "Количество пользователей возвращается неверно");
        assertTrue(likes.contains(user2.getId()));
    }

    @Test
    void getPopularFilmsTest() {
        Film film = filmDbRepository.create(createFilm());
        Film film2 = filmDbRepository.create(createFilm());

        User user1 = User.builder()
                .login("SomeLogin")
                .name("SomeName")
                .email("test@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        user1 = userDbRepository.create(user1);
        User user2 = User.builder()
                .login("SomeLogin2")
                .name("SomeName2")
                .email("test2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        user2 = userDbRepository.create(user2);

        filmDbRepository.addLike(film.getId(), user1.getId());
        filmDbRepository.addLike(film2.getId(), user2.getId());
        filmDbRepository.addLike(film2.getId(), user1.getId());

        List<Film> popularFilms = (List<Film>) filmDbRepository.getPopularFilms(2);

        assertEquals(popularFilms.size(), 2, "Неверное количество фильмов");
        assertEquals(popularFilms.get(0), film2);
        assertEquals(popularFilms.get(1), film);
    }
}
