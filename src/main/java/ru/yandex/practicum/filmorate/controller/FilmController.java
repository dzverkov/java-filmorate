package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/films")
public class FilmController {
    private final HashMap<Integer, Film> films = new HashMap<>();

    @GetMapping
    public List<Film> findAllFilms() {
        return new ArrayList<>(films.values());
    }

    @PostMapping
    public Film createFilm(@RequestBody Film film) {

        log.debug("Получен запрос на добавление нового фильма. Параметры: {}.", film);

        validateFilm(film);
        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        log.debug("Получен запрос на обновление фильма. Параметры: {}.", film);
        if (!films.containsKey(film.getId())) {
            log.error("Фильм с id: " + film.getId() + " не найден.");
            throw new ValidationException("Фильм с id: " + film.getId() + " не найден.");
        }
        validateFilm(film);

        films.put(film.getId(), film);
        log.debug("Обновлён фильм: {}", film);
        return film;
    }

    private void validateFilm(Film film) {
        final int MAX_DESCRIPTION_LENGTH = 200;
        final LocalDate MIN_RELEASED_DATE = LocalDate.of(1895, 12, 28);

        // название не может быть пустым
        if (film.getName().isEmpty() || film.getName().isBlank()) {
            log.error("Наименование фильма пустое.");
            throw new ValidationException("Наименование фильма пустое.");
        }
        // максимальная длина описания — 200 символов
        if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
            log.error("Описание фильма не может превышать " + MAX_DESCRIPTION_LENGTH + " символов.");
            throw new ValidationException("Описание фильма не может превышать " + MAX_DESCRIPTION_LENGTH + " символов.");
        }
        // дата релиза — не раньше 28 декабря 1895 года
        if (film.getReleaseDate().isBefore(MIN_RELEASED_DATE)) {
            log.error("Дата созданияне должна быть больше " + MIN_RELEASED_DATE);
            throw new ValidationException("Дата созданияне должна быть больше " + MIN_RELEASED_DATE);
        }
        // продолжительность фильма должна быть положительной
        if (film.getDuration() <= 0) {
            log.error("Неверная продолжительность фильма: " + film.getDuration());
            throw new ValidationException("Неверная продолжительность фильма: " + film.getDuration());
        }
    }

}
