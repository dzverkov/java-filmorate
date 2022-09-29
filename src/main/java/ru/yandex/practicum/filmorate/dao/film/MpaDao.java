package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

public interface MpaDao {
    List<Mpa> findAllMpa();

    Mpa findMpaById(int id);
}
