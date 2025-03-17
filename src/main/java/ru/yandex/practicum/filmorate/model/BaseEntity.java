package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(of = {"id"})
@ToString(of = {"id"})
@SuperBuilder
@NoArgsConstructor
public abstract class BaseEntity {
    protected Long id;
}
