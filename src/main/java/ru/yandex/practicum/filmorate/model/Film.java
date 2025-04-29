package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@EqualsAndHashCode(of = {"id"})
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true, of = {"name"})
public class Film extends BaseEntity {

    @NotBlank(message = "Название не может быть пустым")
    private String name;

    @NotNull
    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    @NotNull
    @PastOrPresent(message = "Дата релиза не может быть в будущем")
    private LocalDate releaseDate;

    @Min(1)
    private int duration;

    @Builder.Default
    private Set<Long> likes = new HashSet<>();

    @NotNull(message = "У фильма должен быть рейтинг MPA")
    RatingMpa mpa;

    @Builder.Default
    List<Genre> genres = new ArrayList<>();
}