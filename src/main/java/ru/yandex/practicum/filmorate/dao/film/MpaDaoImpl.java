package ru.yandex.practicum.filmorate.dao.film;

import lombok.AllArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.MpaNotFoundException;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class MpaDaoImpl implements MpaDao {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<Mpa> findAllMpa() {
        String sql = "select MPA_ID, NAME from MPA";
        return jdbcTemplate.query(sql, (rs, rowNum) -> makeMpa(rs));
    }

    @Override
    public Mpa findMpaById(int id) {
        String sql = "select MPA_ID, NAME from MPA where MPA_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeMpa(rs), id);
        } catch (EmptyResultDataAccessException ex) {
            throw new MpaNotFoundException("Рейтинг с id = " + id + " не найден.");
        }
    }

    private Mpa makeMpa(ResultSet rs) throws SQLException {
        return new Mpa(
                rs.getInt("MPA_ID")
                , rs.getString("NAME")
        );
    }
}
