package ru.yandex.practicum.filmorate.dao.film;

import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;

public interface GenreDao {

    Genre findGenreById(int id);

    List<Genre> findAllGenres();
}
