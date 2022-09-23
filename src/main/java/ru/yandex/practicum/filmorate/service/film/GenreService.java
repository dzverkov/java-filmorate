package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.storage.film.GenreStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreService {

    private final GenreStorage genreStorage;

    public Genre findGenreById(int id) {
        return genreStorage.findGenreById(id);
    }

    public List<Genre> findAllGenres() {
        return genreStorage.findAllGenres();
    }
}
