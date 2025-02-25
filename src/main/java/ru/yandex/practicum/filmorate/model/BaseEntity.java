package ru.yandex.practicum.filmorate.model;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {
    protected Long id;
}
