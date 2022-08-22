package ru.yandex.practicum.filmorate.service.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.FriendNotFoundException;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserStorage userStorage;

    public List<User> findAllUsers() {
        return userStorage.findAllUsers();
    }

    public User findUserById(int userId) {
        User user = userStorage.findUserById(userId);
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с id = %d не найден.", userId));
        }
        return user;
    }

    public User createUser(User user) {
        User userValidated;
        userValidated = validateName(user);

        int id = userStorage.getNextId();
        userValidated.setId(id);
        userStorage.createUser(userValidated);
        log.debug("Добавлен пользователь: {}", user);
        return user;
    }

    public User updateUser(User user) {
        validateUserAvailability(user);
        User userValidated = validateName(user);

        user = userStorage.updateUser(userValidated);
        log.debug("Обновлён фильм: {}", user);
        return user;
    }

    public void createFriend(int userId, int friendId) {
        validateUserAvailability(userId);
        validateUserAvailability(friendId);

        userStorage.createFriend(userId, friendId);
        log.debug("Добавлен к пользователю с id = {} друг с id = {}.", userId, friendId);
    }

    public void deleteFriend(int userId, int friendId) {
        validateUserAvailability(userId);
        validateUserAvailability(friendId);

        boolean isDeleted = userStorage.deleteFriend(userId, friendId);

        if (!isDeleted) {
            throw new FriendNotFoundException(String.format("У пользователя с id = %d нет друга с id = %d или наоборот."
                    , userId, friendId));
        }
        log.debug("Удалён у пользователя с id = {} друг с id = {}.", userId, friendId);
    }

    public List<User> findUserFriends(int userId) {
        validateUserAvailability(userId);

        return userStorage.findUserFriends(userId);
    }

    public List<User> findUsersFriendsCommon(int userId, int otherId) {
        validateUserAvailability(userId);
        validateUserAvailability(otherId);

        return userStorage.findUsersFriendsCommon(userId, otherId);
    }

    private void validateUserAvailability(User user) {
        validateUserAvailability(user.getId());
    }

    public void validateUserAvailability(int userId) {
        if (!userStorage.contains(userId)) {
            String errorMessage = "Пользователь с id: " + userId + " не найден.";
            log.error(errorMessage);
            throw new UserNotFoundException(errorMessage);
        }
    }

    static User validateName(User user) {
        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            user.setName(user.getLogin());
        }
        return user;
    }

}
