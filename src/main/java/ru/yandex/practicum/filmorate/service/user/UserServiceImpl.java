package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.dao.user.UserDao;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserDao userDao;

    @Override
    public List<User> findAllUsers() {
        return userDao.findAllUsers();
    }

    @Override
    public User findUserById(int userId) {
        User user = userDao.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        return user;
    }

    @Override
    public User createUser(User user) {
        User userValidated;
        userValidated = validateName(user);

        userDao.createUser(userValidated);
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        User userValidated = validateName(user);

        user = userDao.updateUser(userValidated);
        log.debug("Обновлён фильм: {}", user);
        return user;
    }

    @Override
    public void createFriend(int userId, int friendId) {
        userDao.createFriend(userId, friendId);
        log.debug("Добавлен к пользователю с id = {} друг с id = {}.", userId, friendId);
    }

    @Override
    public void deleteFriend(int userId, int friendId) {
        boolean isDeleted = userDao.deleteFriend(userId, friendId);

        if (!isDeleted) {
            throw new FriendNotFoundException(String.format("У пользователя с id = %d нет друга с id = %d или наоборот."
                    , userId, friendId));
        }
        log.debug("Удалён у пользователя с id = {} друг с id = {}.", userId, friendId);
    }

    @Override
    public List<User> findUserFriends(int userId) {
        return userDao.findUserFriends(userId);
    }

    @Override
    public List<User> findUsersFriendsCommon(int userId, int otherId) {
        return userDao.findUsersFriendsCommon(userId, otherId);
    }

    static User validateName(User user) {
        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

}
