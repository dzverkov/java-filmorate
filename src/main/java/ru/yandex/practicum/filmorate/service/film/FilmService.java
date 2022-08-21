package ru.yandex.practicum.filmorate.service.film;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.exception.LikeNotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.user.UserService;
import ru.yandex.practicum.filmorate.storage.film.FilmStorage;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
public class FilmService {
    FilmStorage filmStorage;
    UserService userService;

    @Autowired
    public FilmService(FilmStorage filmStorage, UserService userService) {
        this.filmStorage = filmStorage;
        this.userService = userService;
    }

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
        log.debug("Получен запрос на добавление нового фильма. Параметры: {}.", film);
        validateReleaseDate(film);
        filmStorage.createFilm(film);
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    public Film updateFilm(Film film) {
        log.debug("Получен запрос на обновление фильма. Параметры: {}.", film);

        validateFileAvailability(film);
        validateReleaseDate(film);

        filmStorage.updateFilm(film);
        log.debug("Обновлён фильм: {}", film);
        return film;
    }

    public void addLike(int filmId, int userId) {
        log.debug("Получен запрос на добавление лайка фильму с id = {} от пользователя с id = {}.", filmId, userId);
        validateFileAvailability(filmId);
        userService.validateUserAvailability(userId);

        filmStorage.addLike(filmId, userId);
        log.debug("Добавлен лайк фильму с id = {} от пользователя с id = {}.", filmId, userId);
    }

    public void deleteLike(int filmId, int userId) {
        log.debug("Получен запрос на удаление лайка фильму с id = {} от пользователя с id = {}.", filmId, userId);
        validateFileAvailability(filmId);
        userService.validateUserAvailability(userId);

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

    private void validateFileAvailability(Film film) {
        validateFileAvailability(film.getId());
    }

    private void validateFileAvailability(int filmId) {
        if (!filmStorage.contains(filmId)) {
            String errorMessage = "Фильм с id: " + filmId + " не найден.";
            log.error(errorMessage);
            throw new FilmNotFoundException(errorMessage);
        }
    }

    static void validateReleaseDate(Film film) {
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
