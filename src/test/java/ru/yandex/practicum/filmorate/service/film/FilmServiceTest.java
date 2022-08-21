package ru.yandex.practicum.filmorate.service.film;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FilmServiceTest {

    @Test
    void validateReleaseDate() {
        Film film = new Film();
        film.setName("nisi eiusmod");
        film.setDescription("adipisicing");
        film.setReleaseDate(LocalDate.of(1895, 12, 20));
        film.setDuration(120);

        assertThrows(ValidationException.class,
                () -> FilmService.validateReleaseDate(film));

        film.setReleaseDate(LocalDate.of(1895, 12, 28));
        assertDoesNotThrow(() -> FilmService.validateReleaseDate(film));

        film.setReleaseDate(LocalDate.of(2020, 12, 28));
        assertDoesNotThrow(() -> FilmService.validateReleaseDate(film));
    }
}