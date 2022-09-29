package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.user.UserServiceImpl;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final UserServiceImpl userService;

    @GetMapping
    public List<User> findAllUsers() {
        return userService.findAllUsers();
    }

    @GetMapping(value = "/{userId}")
    public User findUserById(@PathVariable int userId) {
        return userService.findUserById(userId);
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        return userService.updateUser(user);
    }

    @PutMapping(value = "/{userId}/friends/{friendId}")
    public void createFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.createFriend(userId, friendId);
    }

    @DeleteMapping(value = "/{userId}/friends/{friendId}")
    public void deleteFriend(@PathVariable int userId, @PathVariable int friendId) {
        userService.deleteFriend(userId, friendId);
    }

    @GetMapping(value = "/{userId}/friends")
    public List<User> findUserFriends(@PathVariable int userId) {
        return userService.findUserFriends(userId);
    }

    @GetMapping(value = "{userId}/friends/common/{otherId}")
    public List<User> findUsersFriendsCommon(@PathVariable int userId, @PathVariable int otherId) {
        return userService.findUsersFriendsCommon(userId, otherId);
    }

}
