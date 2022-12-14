package ru.yandex.practicum.filmorate.dao.user;

import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserDao {
    List<User> findAllUsers();

    User findUserById(int userId);

    User createUser(User user);

    boolean contains(int userId);

    User updateUser(User user);

    boolean deleteUser(User user);

    void createFriend(int userId, int friendId);

    boolean deleteFriend(int userId, int friendId);

    List<User> findUserFriends(int userId);

    List<User> findUsersFriendsCommon(int userId, int otherId);

}
