package ru.yandex.practicum.filmorate.service.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserService {
    List<User> findAllUsers();

    User findUserById(int userId);

    User createUser(User user);

    User updateUser(User user);

    void createFriend(int userId, int friendId);

    void deleteFriend(int userId, int friendId);

    List<User> findUserFriends(int userId);

    List<User> findUsersFriendsCommon(int userId, int otherId);
}
