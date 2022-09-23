package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.GenreNotFoundException;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class GenreDbStorage implements GenreStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public Genre findGenreById(int id) {
        String sql = "select GENRE_ID, NAME from GENRES where GENRE_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeGenre(rs), id);
        } catch (EmptyResultDataAccessException ex) {
            throw  new GenreNotFoundException("Жанр с id = " + id + " не найден.");
        }
    }

    @Override
    public List<Genre> findAllGenres() {
        String sql = "select GENRE_ID, NAME from GENRES";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeGenre(rs));
    }

    private Genre makeGenre(ResultSet rs) throws SQLException {
        return new Genre(
                rs.getInt("GENRE_ID")
                , rs.getString("NAME")
        );
    }
}
