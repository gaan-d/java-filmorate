![Схема базы данных](https://github.com/gaan-d/java-filmorate/blob/add-database/scheme.png)

# java-filmorate
Template repository for Filmorate project.

### `user`
Содержит информацию о пользователях системы.

| Поле         | Тип        | Описание                                |
|--------------|------------|-----------------------------------------|
| user_id      | bigint     | Первичный ключ (PK), уникальный ID      |
| email        | varchar(50)| Электронная почта пользователя          |
| login        | varchar(20)| Логин пользователя                      |
| name         | varchar(30)| Имя пользователя                        |
| birthday     | date       | Дата рождения (обязательное поле)       |

---

### `film`
Содержит информацию о фильмах.

| Поле               | Тип         | Описание                              |
|--------------------|-------------|---------------------------------------|
| id                 | integer     | Первичный ключ (PK), уникальный ID    |
| name               | varchar     | Название фильма (обязательное)        |
| description        | varchar(200)| Описание фильма                       |
| release_date       | timestamp   | Дата релиза (обязательное поле)       |
| duration_in_minutes| bigint      | Длительность фильма в минутах         |
| rating_id          | bigint      | Ссылка на таблицу `mpa_rating`        |

---

### `mpa_rating`
Справочник рейтингов MPA (Motion Picture Association).

| Поле | Тип     | Описание                            |
|------|---------|-------------------------------------|
| id   | bigint  | Первичный ключ (PK)                 |
| name | varchar | Название рейтинга (G, PG, PG-13 и т.д.) |

---

### `genre`
Справочник жанров фильмов.

| Поле | Тип     | Описание                 |
|------|---------|--------------------------|
| id   | integer | Первичный ключ (PK)      |
| name | varchar | Название жанра           |

---

### `film_genre`
Связывает фильмы с жанрами (многие ко многим).

| Поле     | Тип     | Описание                             |
|----------|---------|--------------------------------------|
| film_id  | bigint  | Внешний ключ на `film.id`            |
| genre_id | integer | Внешний ключ на `genre.id`           |

> Первичный ключ может быть составным: (`film_id`, `genre_id`).

---

### `film_like`
Таблица лайков фильмов пользователями.

| Поле     | Тип     | Описание                               |
|----------|---------|----------------------------------------|
| film_id  | bigint  | Внешний ключ на `film.id`              |
| user_id  | bigint  | Внешний ключ на `user.user_id`         |

> Первичный ключ: (`film_id`, `user_id`).

---

### `friendship`
Хранит информацию о дружбе между пользователями.

| Поле         | Тип     | Описание                                                   |
|--------------|---------|------------------------------------------------------------|
| user_id      | bigint  | ID пользователя, который отправил запрос в друзья         |
| friend_id    | bigint  | ID пользователя, которому отправлен запрос                |
| is_confirmed | boolean | Признак подтверждения дружбы (true — подтверждена, false — нет) |

> Первичный ключ может быть составным: (`user_id`, `friend_id`).

## Основные запросы

### Получение всех фильмов

```sql
SELECT * FROM film;
```

С жанрами и рейтингом MPA:

```sql
SELECT 
  f.*, 
  r.name AS mpa_rating,
  string_agg(g.name, ', ') AS genres
FROM film f
JOIN mpa_rating r ON f.rating_id = r.id
LEFT JOIN film_genre fg ON f.id = fg.film_id
LEFT JOIN genre g ON fg.genre_id = g.id
GROUP BY f.id, r.name;
```

---

### Получение всех пользователей

```sql
SELECT * FROM "user";
```

---

### Топ N наиболее популярных фильмов

```sql
SELECT 
  f.*, 
  COUNT(fl.user_id) AS like_count
FROM film f
LEFT JOIN film_like fl ON f.id = fl.film_id
GROUP BY f.id
ORDER BY like_count DESC
LIMIT N;
```

---

### Список общих друзей между двумя пользователями

```sql
SELECT u.*
FROM "user" u
JOIN friendship f1 ON f1.friend_id = u.id AND f1.is_confirmed = true
JOIN friendship f2 ON f2.friend_id = u.id AND f2.is_confirmed = true
WHERE f1.user_id = :user1_id
  AND f2.user_id = :user2_id;
```
