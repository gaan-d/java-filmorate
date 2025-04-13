[![scheme](https://github.com/gaan-d/java-filmorate/blob/add-database/scheme.png)

# java-filmorate
Template repository for Filmorate project.

## 📚 Основные запросы

### 🎬 Получение всех фильмов

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

### 👤 Получение всех пользователей

```sql
SELECT * FROM "user";
```

---

### 🌟 Топ N наиболее популярных фильмов

> Предполагается наличие таблицы `film_like`, где фиксируются лайки от пользователей.

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

### 🧑‍🤝‍🧑 Список общих друзей между двумя пользователями

```sql
SELECT u.*
FROM "user" u
JOIN friendship f1 ON f1.friend_id = u.user_id AND f1.is_confirmed = true
JOIN friendship f2 ON f2.friend_id = u.user_id AND f2.is_confirmed = true
WHERE f1.user_id = :user1_id
  AND f2.user_id = :user2_id;
```
