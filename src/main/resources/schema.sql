-- Создание таблицы для пользователей
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    email VARCHAR(50) NOT NULL,
    login VARCHAR(50) NOT NULL UNIQUE,
    name VARCHAR(255),
    birthday DATE NOT NULL
);

-- Создание таблицы для рейтингов MPA
CREATE TABLE IF NOT EXISTS mpa_rating (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255)
);

-- Создание таблицы для фильмов
CREATE TABLE IF NOT EXISTS film (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description VARCHAR(200),
    release_date TIMESTAMP NOT NULL,
    duration_in_minutes INT NOT NULL,
    rating_id BIGINT NOT NULL,
    FOREIGN KEY (rating_id) REFERENCES mpa_rating(id)
);

-- Создание таблицы жанров
CREATE TABLE IF NOT EXISTS genre (
    id INT PRIMARY KEY,
    name VARCHAR(255)
);

-- Создание таблицы связей фильмов и жанров (многие ко многим)
CREATE TABLE IF NOT EXISTS film_genre (
    film_id BIGINT,
    genre_id INT,
    PRIMARY KEY (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES film(id),
    FOREIGN KEY (genre_id) REFERENCES genre(id)
);

-- Создание таблицы лайков фильмов пользователями
CREATE TABLE IF NOT EXISTS film_like (
    user_id BIGINT,
    film_id BIGINT,
    PRIMARY KEY (user_id, film_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (film_id) REFERENCES film(id)
);

-- Создание таблицы дружбы (с двумя пользователями)
CREATE TABLE IF NOT EXISTS friendship (
    user_id BIGINT,
    friend_id BIGINT,
    is_confirmed BOOLEAN DEFAULT FALSE,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE,
    FOREIGN KEY (friend_id) REFERENCES users(id)
    ON UPDATE CASCADE ON DELETE CASCADE
);