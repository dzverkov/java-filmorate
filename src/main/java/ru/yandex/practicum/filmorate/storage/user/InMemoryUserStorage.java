package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.User;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryUserStorage implements UserStorage {
    private final HashMap<Integer, User> users = new HashMap<>();

    @Override
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User findUserById(int userId) {
        return users.get(userId);
    }

    @Override
    public User createUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public boolean contains(int userId) {
        return users.containsKey(userId);
    }

    @Override
    public User updateUser(User user) {
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void createFriend(int userId, int friendId) {
        users.get(userId).addFriend(friendId);
        users.get(friendId).addFriend(userId);
    }

    @Override
    public boolean deleteFriend(int userId, int friendId) {
        return users.get(userId).deleteFriend(friendId)
                && users.get(friendId).deleteFriend(userId);
    }

    @Override
    public List<User> findUserFriends(int userId) {
        User user = users.get(userId);
        return user.getFriends().stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public List<User> findUsersFriendsCommon(int userId, int otherId) {
        Set<Integer> userFriendsCommon = new HashSet<>(users.get(userId).getFriends());
        Set<Integer> otherUserFriends = users.get(otherId).getFriends();
        userFriendsCommon.retainAll(otherUserFriends);

        return userFriendsCommon.stream()
                .map(users::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    @Override
    public int getNextId() {
        return users.size() + 1;
    }


}
