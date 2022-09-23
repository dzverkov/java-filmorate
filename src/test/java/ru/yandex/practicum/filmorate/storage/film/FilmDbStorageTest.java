package ru.yandex.practicum.filmorate.storage.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserDbStorage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class FilmDbStorageTest {

    private final FilmDbStorage filmStorage;
    private final UserDbStorage userStorage;

    private Film film1;
    private Film film2;

    @BeforeEach
    void init() {
        film1 = new Film(1,
                "Film 1",
                "Film 1 Description",
                LocalDate.of(2000, 3, 15),
                120,
                4,
                new Mpa(1, "G"),
                List.of()
        );

        film2 = new Film(2,
                "Film 2",
                "Film 2 Description",
                LocalDate.of(2010, 6, 6),
                180,
                4,
                new Mpa(1, "G"),
                List.of()
        );
    }

    @Test
    @Order(3)
    void findAllFilms() {
        List<Film> films = filmStorage.findAllFilms();
        assertTrue(films.contains(film1));
        assertTrue(films.contains(film2));
    }

    @Test
    @Order(2)
    void findFilmById() {
        Film film1FromSt = filmStorage.findFilmById(1);
        Film film2FromSt = filmStorage.findFilmById(2);

        assertEquals(film1, film1FromSt);
        assertEquals(film2, film2FromSt);

    }

    @Test
    @Order(1)
    void createFilm() {
        Film film1FromSt = filmStorage.createFilm(film1);
        Film film2FromSt = filmStorage.createFilm(film2);

        assertEquals(film1, film1FromSt);
        assertEquals(film2, film2FromSt);
    }

    @Test
    @Order(3)
    void contains() {
        assertTrue(filmStorage.contains(1));
        assertTrue(filmStorage.contains(2));
    }

    @Test
    @Order(4)
    void updateFilm() {
        Film film1FromSt = filmStorage.findFilmById(1);
        assertEquals(film1, film1FromSt);
        film1.setName("Film 11");
        film1.setDescription("Film 11 Description");
        film1.setReleaseDate(LocalDate.of(2020, 3, 25));
        film1.setDuration(120);
        List<Genre> genres = new ArrayList<>();
        genres.add(new Genre(1, "Комедия"));
        genres.add(new Genre(2, "Драма"));
        genres.add(new Genre(3, "Мультфильм"));
        film1.setGenres(genres);
        filmStorage.updateFilm(film1);

        Film film11FromSt = filmStorage.findFilmById(1);
        assertEquals(film1, film11FromSt);
        filmStorage.updateFilm(film1FromSt);

        film1.setId(-1);
        assertThrows(FilmNotFoundException.class,
                () -> filmStorage.updateFilm(film1)
        );

    }

    @Test
    @Order(5)
    void addLike() {
        User user1 = new User(1, "user1@mail.com", "user1", "User 1"
                , LocalDate.of(1980, 5, 15));
        user1 = userStorage.createUser(user1);

        filmStorage.addLike(film1.getId(), user1.getId());
        assertTrue(filmStorage.deleteLike(film1.getId(), user1.getId()));
    }

    @Test
    @Order(6)
    void deleteLike() {
        filmStorage.addLike(1, 1);
        assertTrue(filmStorage.deleteLike(1, 1));
        assertFalse(filmStorage.deleteLike(1, 1));
    }

    @Test
    void findTopPopularFilms() {
        for (int i = 2; i <= 10; i++) {
            userStorage.createUser(new User(i, "user" + i + "@mail.com", "user" + i, "User " + i
                    , LocalDate.of(1980 + i, 5, 15 + i)));
            filmStorage.addLike(1, i);
            if (i % 2 == 0) {
                filmStorage.addLike(2, i);
            }
        }
        List<Film> films = filmStorage.findTopPopularFilms(5);
        assertEquals(film1.getId(), films.get(0).getId());
        assertEquals(film2.getId(), films.get(1).getId());
    }
}