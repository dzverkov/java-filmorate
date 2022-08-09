package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import javax.validation.Valid;
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
    public Film createFilm(@Valid @RequestBody Film film) {

        log.debug("Получен запрос на добавление нового фильма. Параметры: {}.", film);

        validateReleaseDate(film);

        film.setId(films.size() + 1);
        films.put(film.getId(), film);
        log.debug("Добавлен фильм: {}", film);
        return film;
    }

    @PutMapping
    public Film updateFilm(@Valid @RequestBody Film film) {
        log.debug("Получен запрос на обновление фильма. Параметры: {}.", film);

        if (!films.containsKey(film.getId())) {
            String errorMessage = "Фильм с id: " + film.getId() + " не найден.";
            log.error(errorMessage);
            throw new ValidationException(errorMessage);
        }

        validateReleaseDate(film);

        films.put(film.getId(), film);
        log.debug("Обновлён фильм: {}", film);
        return film;
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
