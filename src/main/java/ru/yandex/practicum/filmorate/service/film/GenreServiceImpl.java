package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.dao.film.GenreDao;

import java.util.List;

@RequiredArgsConstructor
@Service
public class GenreServiceImpl implements GenreService {

    private final GenreDao genreDao;

    @Override
    public Genre findGenreById(int id) {
        return genreDao.findGenreById(id);
    }

    @Override
    public List<Genre> findAllGenres() {
        return genreDao.findAllGenres();
    }
}
