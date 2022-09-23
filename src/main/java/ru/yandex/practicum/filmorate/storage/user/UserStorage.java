package ru.yandex.practicum.filmorate.storage.user;

import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

public interface UserStorage {
    List<User> findAllUsers();

    User findUserById(int userId);

    User createUser(User user);

    boolean contains(int userId);

    User updateUser(User user);

    void createFriend(int userId, int friendId);

    boolean deleteFriend(int userId, int friendId);

    List<User> findUserFriends(int userId);

    List<User> findUsersFriendsCommon(int userId, int otherId);

}
