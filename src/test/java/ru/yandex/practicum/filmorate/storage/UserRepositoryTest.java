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
import ru.yandex.practicum.filmorate.dal.storage.user.UserRepository;
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
@Import({UserRepository.class})
@ContextConfiguration(classes = {FilmorateApplication.class})
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserRepositoryTest {
    UserRepository userRepository;


    @Test
    void createTest() {
        User user = createUser();
        User newUser = userRepository.create(user);

        assertThat(newUser).hasFieldOrPropertyWithValue("id", newUser.getId());
        assertThat(newUser).hasFieldOrPropertyWithValue("name", user.getName());
        assertThat(newUser).hasFieldOrPropertyWithValue("login", user.getLogin());
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", user.getBirthday());
        assertThat(newUser).hasFieldOrPropertyWithValue("email", user.getEmail());

        assertThat(newUser).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(newUser).hasFieldOrPropertyWithValue("name", "Name");
        assertThat(newUser).hasFieldOrPropertyWithValue("login", "Login");
        assertThat(newUser).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 9, 9));
        assertThat(newUser).hasFieldOrPropertyWithValue("email", "test@mail.ru");
    }

    private User createUser() {
        return User
                .builder()
                .name("Name")
                .login("Login")
                .birthday(LocalDate.of(1999, 9, 9))
                .email("test@mail.ru")
                .build();
    }

    @Test
    void getByIdTest() {
        User user = createUser();
        User newUser = userRepository.create(user);
        User userById = userRepository.getById(newUser.getId());

        assertThat(userById).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(userById).hasFieldOrPropertyWithValue("name", "Name");
        assertThat(userById).hasFieldOrPropertyWithValue("login", "Login");
        assertThat(userById).hasFieldOrPropertyWithValue("birthday", LocalDate.of(1999, 9, 9));
        assertThat(userById).hasFieldOrPropertyWithValue("email", "test@mail.ru");
    }

    @Test
    void getAllTest() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        Map<Long, User> collection = userRepository.getAll();

        assertEquals(collection.size(), 2, "Количество возвращено неверно");
        assertEquals(collection.get(1L), user1, "user1 возвращается неверно");
        assertEquals(collection.get(2L), user2, "user2 возвращается неверно");
    }

    @Test
    void getAllValuesTest() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        List<User> collection = userRepository.getAllValues();

        assertEquals(collection.size(), 2, "Количество возвращено неверно");
        assertEquals(collection.get(0), user1, "user1 возвращается неверно");
        assertEquals(collection.get(1), user2, "user2 возвращается неверно");
    }

    @Test
    void updateTest() {
        User user = userRepository.create(createUser());
        User userUpdate = User.builder()
                .id(user.getId())
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.update(userUpdate);
        user = userRepository.getById(1L);

        assertThat(user).hasFieldOrPropertyWithValue("id", 1L);
        assertThat(user).hasFieldOrPropertyWithValue("name", "Name2");
        assertThat(user).hasFieldOrPropertyWithValue("login", "Login2");
        assertThat(user).hasFieldOrPropertyWithValue("birthday", LocalDate.of(2000, 8, 19));
        assertThat(user).hasFieldOrPropertyWithValue("email", "email2@mail.ru");
    }

    @Test
    void deleteByIdTest() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        userRepository.deleteById(1L);
        Map<Long, User> collection = userRepository.getAll();

        assertEquals(collection.size(), 1, "Количество возвращено неверно");
        assertEquals(collection.get(2L), user2, "user2 возвращается неверно");
    }

    @Test
    void addFriendTest() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        userRepository.addFriend(user1.getId(), user2.getId());

        Set<Long> friends = userRepository.getFriendIds(user1.getId());

        assertEquals(friends.size(), 1, "Количество друзей возвращается неверно");
        assertTrue(friends.contains(user2.getId()));
    }

    @Test
    void removeFriendTest() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        User user3 = User.builder()
                .login("Login3")
                .name("Name3")
                .email("email3@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        userRepository.create(user3);

        userRepository.addFriend(user1.getId(), user2.getId());
        userRepository.addFriend(user1.getId(), user3.getId());

        userRepository.removeFriend(user1.getId(), user2.getId());

        Set<Long> friends = userRepository.getFriendIds(user1.getId());

        assertEquals(friends.size(), 1, "Количество друзей возвращается неверно");
        assertTrue(friends.contains(user3.getId()));
    }

    @Test
    void getFriendIds() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        User user3 = User.builder()
                .login("Login3")
                .name("Name3")
                .email("email3@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        userRepository.create(user3);

        userRepository.addFriend(user1.getId(), user2.getId());
        userRepository.addFriend(user1.getId(), user3.getId());

        Set<Long> friends = userRepository.getFriendIds(user1.getId());

        assertEquals(friends.size(), 2, "Количество друзей возвращается неверно");
        assertTrue(friends.contains(user2.getId()));
        assertTrue(friends.contains(user3.getId()));
    }

    @Test
    void getMutualFriends() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        User user3 = User.builder()
                .login("Login3")
                .name("Name3")
                .email("email3@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        userRepository.create(user3);

        userRepository.addFriend(user1.getId(), user2.getId());
        userRepository.addFriend(user2.getId(), user1.getId());
        userRepository.addFriend(user3.getId(), user2.getId());

        List<User> friends = userRepository.getMutualFriends(user1.getId(), user3.getId());

        assertEquals(friends.size(), 1, "Количество друзей возвращается неверно");
        assertTrue(friends.contains(user2));

        friends = userRepository.getMutualFriends(user2.getId(), user3.getId());

        assertEquals(friends.size(), 0, "Количество друзей возвращается неверно");
    }

    @Test
    void getAllFriends() {
        User user1 = userRepository.create(createUser());
        User user2 = User.builder()
                .login("Login2")
                .name("Name2")
                .email("email2@mail.ru")
                .birthday(LocalDate.of(2000, 8, 19))
                .build();
        userRepository.create(user2);
        User user3 = User.builder()
                .login("Login3")
                .name("Name3")
                .email("email3@mail.ru")
                .birthday(LocalDate.of(2000, 10, 19))
                .build();
        userRepository.create(user3);

        userRepository.addFriend(user1.getId(), user2.getId());
        userRepository.addFriend(user1.getId(), user3.getId());

        Set<Long> friends = userRepository.getFriendIds(user1.getId());

        assertEquals(friends.size(), 2, "Количество друзей возвращается неверно");
        assertTrue(friends.contains(user2.getId()));
        assertTrue(friends.contains(user3.getId()));
    }
}