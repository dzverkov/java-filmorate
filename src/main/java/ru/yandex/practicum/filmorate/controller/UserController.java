package ru.yandex.practicum.filmorate.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
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
    public User createUser(@RequestBody User user) {

        log.debug("Получен запрос на добавление нового пользователя. Параметры: {}.", user);
        User userValidated = validateUser(user);

        userValidated.setId(users.size() + 1);
        users.put(userValidated.getId(), userValidated);
        log.debug("Добавлен пользователь: {}", user);
        return userValidated;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {

        log.debug("Получен запрос на обновление пользователя. Параметры: {}.", user);
        if (!users.containsKey(user.getId())) {
            log.error("Пользователь с id: " + user.getId() + " не найден.");
            throw new ValidationException("Пользователь с id: " + user.getId() + " не найден.");
        }

        User userValidated = validateUser(user);

        users.put(userValidated.getId(), userValidated);
        log.debug("Обновлён фильм: {}", user);
        return userValidated;
    }

    private User validateUser(User user) {
        User userValidated = user;
        // электронная почта не может быть пустой и должна содержать символ @
        if (user.getEmail().isEmpty() || user.getEmail().isBlank() || !user.getEmail().contains("@")) {
            log.error("Неверно указан email: " + user.getEmail());
            throw new ValidationException("Неверно указан email: " + user.getEmail());
        }
        // логин не может быть пустым и содержать пробелы
        if (user.getLogin().isEmpty() || user.getLogin().isBlank() || user.getLogin().contains(" ")) {
            log.error("Логин не может быть пустым и содержать пробелы: " + user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы: " + user.getLogin());
        }
        // имя для отображения может быть пустым — в таком случае будет использован логин
        if (user.getName().isEmpty() || user.getName().isBlank()) {
            log.warn("Имя для отображения может быть пустым, использовано значение логина.");
            userValidated.setName(userValidated.getLogin());
        }
        // дата рождения не может быть в будущем
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Дата рождения не может быть в будущем, birthday: " + user.getBirthday());
            throw new ValidationException("Неверно указан birthday: " + user.getBirthday());
        }
        return userValidated;
    }
}
