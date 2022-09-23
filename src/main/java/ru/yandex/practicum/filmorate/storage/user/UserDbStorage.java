package ru.yandex.practicum.filmorate.storage.user;

import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Component
@AllArgsConstructor
public class UserDbStorage implements UserStorage {

    private JdbcTemplate jdbcTemplate;

    @Override
    public List<User> findAllUsers() {
        String sql = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY from USERS";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs));
    }

    @Override
    public User findUserById(int userId) {
        String sql = "select USER_ID, EMAIL, LOGIN, NAME, BIRTHDAY from USERS " +
                "where USER_ID = ?";
        try {
            return jdbcTemplate.queryForObject(sql, (rs, rowNum) -> makeUser(rs), userId);
        } catch (EmptyResultDataAccessException ex) {
            return null;
        }
    }

    @Override
    public User createUser(User user) {
        String sql = "insert into USERS (EMAIL, LOGIN, NAME, BIRTHDAY)" +
                "values (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement(sql, new String[]{"USER_ID"});
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getLogin());
            stmt.setString(3, user.getName());
            stmt.setDate(4, Date.valueOf(user.getBirthday()));
            return stmt;
        }, keyHolder);
        user.setId(keyHolder.getKey().intValue());
        return user;
    }

    @Override
    public boolean contains(int userId) {
        User user = findUserById(userId);

        return user != null;
    }

    @Override
    public User updateUser(User user) {

        if (!contains(user.getId())) {
            throw new UserNotFoundException("Пользоватетель с id=" + user.getId() + " не найден.");
        }

        String sql = "update USERS\n" +
                "set EMAIL = ?\n" +
                "  , LOGIN = ?\n" +
                "  , NAME = ?\n" +
                "  , BIRTHDAY = ?\n" +
                "where USER_ID = ?";

        jdbcTemplate.update(sql
                , user.getEmail()
                , user.getLogin()
                , user.getName()
                , Date.valueOf(user.getBirthday())
                , user.getId()
        );
        return user;
    }

    @Override
    public void createFriend(int userId, int friendId) {

        String sql = "merge into FRIENDS (USER_ID, FRIEND_ID) KEY (USER_ID, FRIEND_ID) values (?, ?)";
        try {
            jdbcTemplate.update(sql
                    , userId
                    , friendId
            );
        } catch (DataAccessException ex) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        String sql = "delete from FRIENDS where USER_ID = ? and FRIEND_ID = ?";
        int rowCount;
        try {
            rowCount = jdbcTemplate.update(sql
                    , userId
                    , friendId
            );
        } catch (DataAccessException ex) {
            throw new UserNotFoundException("Пользователь не найден.");
        }
        return rowCount > 0;
    }

    @Override
    public List<User> findUserFriends(int userId) {
        String sql = "select U.USER_ID, U.EMAIL, U.LOGIN, U.NAME, U.BIRTHDAY \n" +
                "from USERS U \n" +
                "join FRIENDS F on U.USER_ID = F.FRIEND_ID\n" +
                "where F.USER_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId);
    }

    @Override
    public List<User> findUsersFriendsCommon(int userId, int otherId) {
        String sql = "WITH\n" +
                "    USER_FRIENDS AS (\n" +
                "        SELECT\n" +
                "            USER_ID\n" +
                "             , FRIEND_ID\n" +
                "        FROM FRIENDS F\n" +
                "        WHERE IS_CONFIRMED = /* <> */ 0\n" +
                "    )\n" +
                "SELECT\n" +
                "    U.USER_ID\n" +
                "     , U.EMAIL\n" +
                "     , U.LOGIN\n" +
                "     , U.NAME\n" +
                "     , U.BIRTHDAY\n" +
                "FROM USERS U\n" +
                "         JOIN USER_FRIENDS UF1 ON U.USER_ID = UF1.FRIEND_ID\n" +
                "         JOIN USER_FRIENDS UF2 ON UF1.FRIEND_ID = UF2.FRIEND_ID\n" +
                "    AND UF1.FRIEND_ID <> UF2.USER_ID AND UF2.FRIEND_ID <> UF1.USER_ID\n" +
                "WHERE UF1.USER_ID = ?\n" +
                "  AND  UF2.USER_ID = ?";

        return jdbcTemplate.query(sql, (rs, rowNum) -> makeUser(rs), userId, otherId);
    }

    private User makeUser(ResultSet rs) throws SQLException {
        return new User(rs.getInt("USER_ID"),
                rs.getString("EMAIL"),
                rs.getString("LOGIN"),
                rs.getString("NAME"),
                rs.getDate("BIRTHDAY").toLocalDate()
        );
    }
}
