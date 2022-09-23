package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.exception.UserNotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class UserDbStorageTest {

    private final UserDbStorage userStorage;

    static List<User> users = new ArrayList<>();

    @BeforeAll
    static void init() {
        for (int i = 1; i <= 10; i++) {
            users.add(new User(i, "user" + i + "@mail.com", "user" + i, "User " + i
                    , LocalDate.of(1980 + i, 5, 15 + i)));
        }
    }

    @Test
    @Order(3)
    void findAllUsers() {
        List<User> usersDb = userStorage.findAllUsers();
        assertEquals(users, usersDb);
    }

    @Test
    @Order(2)
    void findUserById() {
        for (User user : users) {
            assertEquals(user, userStorage.findUserById(user.getId()));
        }
    }

    @Test
    @Order(1)
    void createUser() {
        for (User user : users) {
            userStorage.createUser(user);
        }
        assertEquals(users.get(0), userStorage.findUserById(users.get(0).getId()));
    }

    @Test
    @Order(3)
    void contains() {
        for (User user : users) {
            assertTrue(userStorage.contains(user.getId()));
        }
        assertFalse(userStorage.contains(0));
    }

    @Test
    @Order(4)
    void updateUser() {
        User user1FromSt = userStorage.findUserById(users.get(0).getId());
        assertEquals(users.get(0), user1FromSt);

        User updUser = new User(user1FromSt.getId(), "user_upd@mail.com", "user_upd", "User Upd"
                , LocalDate.of(1970, 1, 1));
        userStorage.updateUser(updUser);
        User userUpdFromSt = userStorage.findUserById(users.get(0).getId());
        assertEquals(updUser, userUpdFromSt);
        userStorage.updateUser(user1FromSt);

        updUser.setId(-1);
        assertThrows(UserNotFoundException.class,
                () -> userStorage.updateUser(updUser));
    }

    @Test
    @Order(5)
    void createFriend() {

        assertThrows(UserNotFoundException.class,
                () -> userStorage.createFriend(users.get(0).getId(), -1));
        userStorage.createFriend(users.get(0).getId(), users.get(1).getId());
        assertTrue(userStorage.deleteFriend(users.get(0).getId(), users.get(1).getId()));
    }

    @Test
    @Order(6)
    void deleteFriend() {
        userStorage.createFriend(users.get(0).getId(), users.get(1).getId());
        assertTrue(userStorage.deleteFriend(users.get(0).getId(), users.get(1).getId()));
        assertFalse(userStorage.deleteFriend(users.get(0).getId(), users.get(1).getId()));
    }

    @Test
    @Order(7)
    void findUserFriends() {
        List<User> userFriends;
        userFriends = userStorage.findUserFriends(users.get(0).getId());
        assertEquals(0, userFriends.size());
        userStorage.createFriend(users.get(0).getId(), users.get(1).getId());
        userStorage.createFriend(users.get(0).getId(), users.get(2).getId());
        userStorage.createFriend(users.get(0).getId(), users.get(3).getId());
        userFriends = userStorage.findUserFriends(users.get(0).getId());
        assertEquals(3, userFriends.size());
        assertTrue(userFriends.contains(users.get(1)));
        assertTrue(userFriends.contains(users.get(2)));
        assertTrue(userFriends.contains(users.get(3)));

        userStorage.deleteFriend(users.get(0).getId(), users.get(1).getId());
        userStorage.deleteFriend(users.get(0).getId(), users.get(2).getId());
        userStorage.deleteFriend(users.get(0).getId(), users.get(3).getId());
    }

    @Test
    @Order(8)
    void findUsersFriendsCommon() {


        for (int i = 2; i < users.size(); i++) {
            if(i % 2 != 0){
                userStorage.createFriend(users.get(0).getId(), users.get(i).getId());
            }
            if(i % 2 == 0 || i % 3 == 0){
                userStorage.createFriend(users.get(1).getId(), users.get(i).getId());
            }
        }

        List<User> friends = userStorage.findUsersFriendsCommon(users.get(0).getId(), users.get(1).getId());
       assertEquals(2, friends.size());
    }
}