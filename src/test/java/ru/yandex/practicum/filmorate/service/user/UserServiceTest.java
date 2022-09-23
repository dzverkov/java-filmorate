package ru.yandex.practicum.filmorate.service.user;

import org.junit.jupiter.api.Test;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserServiceTest {

    @Test
    void validateName() {
        User user = new User();
        user.setEmail("mail@mail.ru");
        user.setLogin("dolore");
        user.setBirthday(LocalDate.of(2000, 03, 15));

        user = UserService.validateName(user);
        assertEquals(user.getLogin(), user.getName());
    }
}