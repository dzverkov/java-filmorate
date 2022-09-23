package ru.yandex.practicum.filmorate.storage.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.FilmNotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class FilmDbStorage implements FilmStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Film> findAllFilms() {

        String sql = "with GEN as (\n" +
                "    select FG.FILM_ID, LISTAGG(G.GENRE_ID || ',' || G.NAME, ';') WITHIN GROUP (ORDER BY FG.FILM_ID) GENRES\n" +
                "    from FILM_GENRES FG\n" +
                "        join GENRES G on FG.GENRE_ID = G.GENRE_ID\n" +
                "    group by FG.FILM_ID\n" +
                ")\n" +
                "select F.FILM_ID\n" +
                "     , F.NAME\n" +
                "     , F.DESCRIPTION\n" +
                "     , F.RELEASE_DATE\n" +
                "     , F.DURATION\n" +
                "     , F.RATE\n" +
                "     , F.MPA_ID\n" +
                "     , R.NAME MPA\n" +
                "     , G.GENRES\n" +
                "from FILMS F\n" +
                "         left join MPA R on R.MPA_ID = F.MPA_ID\n" +
                "         left join GEN G on F.FILM_ID = G.FILM_ID";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs));
    }

    @Override
    public Film findFilmById(int filmId) {
        String sql = "with GEN as (\n" +
                "    select FG.FILM_ID, LISTAGG(G.GENRE_ID || ',' || G.NAME, ';') WITHIN GROUP (ORDER BY FG.FILM_ID) GENRES\n" +
                "    from FILM_GENRES FG\n" +
                "        join GENRES G on FG.GENRE_ID = G.GENRE_ID\n" +
                "    group by FG.FILM_ID\n" +
                ")\n" +
                "select F.FILM_ID\n" +
                "     , F.NAME\n" +
                "     , F.DESCRIPTION\n" +
                "     , F.RELEASE_DATE\n" +
                "     , F.DURATION\n" +
                "     , F.RATE\n" +
                "     , F.MPA_ID \n" +
                "     , R.NAME MPA\n" +
                "     , G.GENRES\n" +
                "from FILMS F\n" +
                "         left join MPA R on R.MPA_ID = F.MPA_ID\n" +
                "         left join GEN G on F.FILM_ID = G.FILM_ID\n" +
                "where F.FILM_ID = ?";

        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeFilm(rs), filmId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public Film createFilm(Film film) {

        String sql = "insert into FILMS (NAME, DESCRIPTION, RELEASE_DATE, DURATION, RATE, MPA_ID)\n" +
                "values (?, ?, ?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"FILM_ID"});
            stmt.setString(1, film.getName());
            stmt.setString(2, film.getDescription());
            stmt.setDate(3, Date.valueOf(film.getReleaseDate()));
            stmt.setInt(4, film.getDuration());
            stmt.setInt(5, film.getRate());
            stmt.setInt(6, film.getMpa().getId());
            return stmt;
        }, keyHolder);
        film.setId(keyHolder.getKey().intValue());
        insertFilmGenres(film);

        return findFilmById(film.getId());
    }

    @Override
    public boolean contains(int filmId) {
        Film film = findFilmById(filmId);

        return film != null;
    }

    @Override
    public Film updateFilm(Film film) {

        if (!contains(film.getId())) {
            throw new FilmNotFoundException("Фильм с id=" + film.getId() + " не найден.");
        }

        String updateFilmSql = "update FILMS\n" +
                "set NAME = ?\n" +
                "  , DESCRIPTION = ?\n" +
                "  , RELEASE_DATE = ?\n" +
                "  , DURATION = ?\n" +
                "  , RATE = ?\n" +
                "  , MPA_ID = ?\n" +
                "where FILM_ID = ?";

        jdbcTemplate.update(updateFilmSql
                , film.getName()
                , film.getDescription()
                , Date.valueOf(film.getReleaseDate())
                , film.getDuration()
                , film.getRate()
                , film.getMpa().getId()
                , film.getId()
        );

        String deleteFilmGenresSql = "delete from FILM_GENRES where FILM_ID = ?";
        jdbcTemplate.update(deleteFilmGenresSql, film.getId());

        insertFilmGenres(film);
        return findFilmById(film.getId());
    }

    @Override
    public void addLike(int filmId, int userId) {
        String sql = "merge into FILM_LIKES (FILM_ID, USER_ID) KEY (FILM_ID, USER_ID) values (?, ?)";

        try {
            jdbcTemplate.update(sql
                    , filmId
                    , userId
            );
        } catch (DataAccessException ex) {
            throw new FilmNotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public boolean deleteLike(int filmId, int userId) {
        String sql = "delete from FILM_LIKES where FILM_ID = ? and USER_ID = ?";
        int rowCount;
        try {
            rowCount = jdbcTemplate.update(sql
                    , filmId
                    , userId
            );
        } catch (DataAccessException ex) {
            return false;
        }
        return rowCount > 0;
    }

    @Override
    public List<Film> findTopPopularFilms(int count) {
        String sql = "with TOP_FILMS AS (\n" +
                "    select F.FILM_ID from FILM_LIKES FL\n" +
                " right join FILMS F on FL.FILM_ID = F.FILM_ID\n" +
                "GROUP BY\n" +
                "    F.FILM_ID, F.RATE\n" +
                "ORDER BY F.RATE + COUNT(FL.USER_ID) DESC\n" +
                "LIMIT ?\n" +
                "),\n" +
                "GEN as (\n" +
                "    select FG.FILM_ID, LISTAGG(G.GENRE_ID || ',' || G.NAME, ';') WITHIN GROUP (ORDER BY FG.FILM_ID) GENRES\n" +
                "    from FILM_GENRES FG\n" +
                "        join GENRES G on FG.GENRE_ID = G.GENRE_ID\n" +
                "    group by FG.FILM_ID\n" +
                ")\n" +
                "select F.FILM_ID\n" +
                "     , F.NAME\n" +
                "     , F.DESCRIPTION\n" +
                "     , F.RELEASE_DATE\n" +
                "     , F.DURATION\n" +
                "     , F.RATE\n" +
                "     , F.MPA_ID \n" +
                "     , R.NAME MPA\n" +
                "     , G.GENRES\n" +
                "from FILMS F\n" +
                "         left join MPA R on R.MPA_ID = F.MPA_ID\n" +
                "         left join GEN G on F.FILM_ID = G.FILM_ID\n" +
                "where F.FILM_ID = ANY (select FILM_ID from TOP_FILMS)";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeFilm(rs), count);
    }

    private Film makeFilm(ResultSet rs) throws SQLException {
        List<Genre> genres;
        String genresStr = rs.getString("GENRES");
        if (genresStr != null) {
            genres = Arrays.stream(genresStr
                            .split(";"))
                    .map(s -> {
                        String[] genre = s.split(",");
                        return new Genre(Integer.parseInt(genre[0]), genre[1]);
                    })
                    .collect(Collectors.toList());
        } else {
            genres = List.of();
        }
        return new Film(rs.getInt("FILM_ID"),
                rs.getString("NAME"),
                rs.getString("DESCRIPTION"),
                rs.getDate("RELEASE_DATE").toLocalDate(),
                rs.getInt("DURATION"),
                rs.getInt("RATE"),
                new Mpa(rs.getInt("MPA_ID"),
                        rs.getString("MPA")
                ),
                genres
        );
    }

    private void insertFilmGenres(Film film) {
        if (film.getGenres() != null && film.getGenres().size() > 0) {
            String insertFilmGenresSql = "insert into FILM_GENRES(FILM_ID, GENRE_ID) \n" +
                    "select ? FILM_ID, GENRE_ID\n" +
                    "       from GENRES WHERE GENRE_ID IN (%s)";
            List<Integer> paramList = new ArrayList<>();
            paramList.add(film.getId());
            paramList.addAll(film.getGenres().stream().map(Genre::getId).collect(Collectors.toList()));
            String inSql = String.join(",", Collections.nCopies(film.getGenres().size(), "?"));
            jdbcTemplate.update(String.format(insertFilmGenresSql, inSql), paramList.toArray());
        }
    }

}
