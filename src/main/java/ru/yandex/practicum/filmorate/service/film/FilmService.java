package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class FilmService {

    private final FilmStorage filmStorage;

    public List<Film> findAllFilms() {
        return filmStorage.findAllFilms();
    }

    public Film findFilmById(int filmId) {
        Film film = filmStorage.findFilmById(filmId);
        if (film == null) {
            throw new FilmNotFoundException(String.format("Фильм с id = %d не найден.", filmId));
        }
        return film;
    }

    public Film createFilm(Film film) {
        validateReleaseDate(film);

        film = filmStorage.createFilm(film);
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        validateReleaseDate(film);

        film = filmStorage.updateFilm(film);
        log.debug("Обновлён фильм: {}", film);
        return film;
    }

    public void addLike(int filmId, int userId) {
        filmStorage.addLike(filmId, userId);
        log.debug("Добавлен лайк фильму с id = {} от пользователя с id = {}.", filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        boolean isDeleted = filmStorage.deleteLike(filmId, userId);

        if (!isDeleted) {
            throw new LikeNotFoundException(String.format("У фильма с id = %d нет лайка от пользователя с id = %d."
                    , filmId, userId));
        }
        log.debug("Удалён лайк фильму с id = {} от пользователя с id = {}.", filmId, userId);
    }

    public List<Film> findTopPopularFilms(int count) {

        return filmStorage.findTopPopularFilms(count);
    }

    public static void validateReleaseDate(Film film) {
        final LocalDate MIN_RELEASED_DATE = LocalDate.of(1895, 12, 28);

        // дата релиза должна быть заполнена
        if (film.getReleaseDate() == null) {
            throw new ValidationException("Дата релиза должна быть заполнена");
        }
        // дата релиза — не раньше 28 декабря 1895 года
        if (film.getReleaseDate().isBefore(MIN_RELEASED_DATE)) {
            throw new ValidationException("Дата релиза должна быть больше " + MIN_RELEASED_DATE);
        }
    }
}
