package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/users")
public class UserController {

    private final HashMap<Integer, User> users = new HashMap<>();

    @GetMapping
    public List<User> findAllUsers() {
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        User userValidated;
        log.debug("Получен запрос на добавление нового пользователя. Параметры: {}.", user);

        userValidated = validateName(user);

        userValidated.setId(users.size() + 1);
        users.put(userValidated.getId(), userValidated);
        log.debug("Добавлен пользователь: {}", user);
        return userValidated;
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {

        log.debug("Получен запрос на обновление пользователя. Параметры: {}.", user);

        try {
            if (!users.containsKey(user.getId())) {
                throw new ValidationException("Пользователь с id: " + user.getId() + " не найден.");
            }
        } catch (ValidationException ex) {
            log.error(ex.getMessage());
            throw new ValidationException();
        }
        User userValidated = validateName(user);

        users.put(userValidated.getId(), userValidated);
        log.debug("Обновлён фильм: {}", user);
        return userValidated;
    }

    static User validateName(User user) {
        User userValidated = user;

        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName() == null || user.getName().isEmpty() || user.getName().isBlank()) {
            userValidated.setName(userValidated.getLogin());
        }
        return userValidated;
    }
}
