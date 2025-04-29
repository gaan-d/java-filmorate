package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import ru.yandex.practicum.filmorate.dal.storage.mpa.MpaRepository;
import ru.yandex.practicum.filmorate.model.RatingMpa;

import java.util.Collection;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RatingService {
    MpaRepository mpaRepository;

    public Collection<RatingMpa> getAllRatings() {
        return mpaRepository.getAllRatings();
    }

    public RatingMpa getRatingById(@PathVariable("id") int id) {
        return mpaRepository.getRatingById(id);
    }
}