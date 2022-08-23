package ru.yandex.practicum.filmorate.storage.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmStorage {
    List<Film> findAllFilms();

    Film findFilmById(int filmId);

    Film createFilm(Film film);

    boolean contains(int filmId);

    Film updateFilm(Film film);


    void addLike(int filmId, int userId);

    boolean deleteLike(int filmId, int userId);

    List<Film> findTopPopularFilms(int count);

    int getNextId();
}
