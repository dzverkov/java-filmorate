package ru.yandex.practicum.filmorate.service.film;

import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

public interface FilmService {
    List<Film> findAllFilms();

    Film findFilmById(int filmId);

    Film createFilm(Film film);

    Film updateFilm(Film film);

    void addLike(int filmId, int userId);

    void deleteLike(int filmId, int userId);

    List<Film> findTopPopularFilms(int count);
}
