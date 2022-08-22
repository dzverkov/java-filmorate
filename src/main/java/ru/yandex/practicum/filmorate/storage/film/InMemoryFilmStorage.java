package ru.yandex.practicum.filmorate.storage.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class InMemoryFilmStorage implements FilmStorage {

    private final HashMap<Integer, Film> films = new HashMap<>();

    @Override
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @Override
    public Film findFilmById(int filmId) {
        return films.get(filmId);
    }

    @Override
    public Film createFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public boolean contains(int filmId) {
        return films.containsKey(filmId);
    }

    @Override
    public Film updateFilm(Film film) {
        films.put(film.getId(), film);
        return film;
    }

    @Override
    public void addLike(int filmId, int userId) {
        films.get(filmId).addLike(userId);
    }

    @Override
    public boolean deleteLike(int filmId, int userId) {
        return films.get(filmId).deleteLike(userId);
    }

    @Override
    public List<Film> findTopPopularFilms(int count) {
        return films.values().stream()
                .sorted(this::topPopularFilmsCompare)
                .limit(count)
                .collect(Collectors.toList());
    }

    @Override
    public int getNextId() {
        return films.size() + 1;
    }

    private int topPopularFilmsCompare(Film f0, Film f1) {
        return -1 * (f0.getLikes().size() - f1.getLikes().size());
    }
}
