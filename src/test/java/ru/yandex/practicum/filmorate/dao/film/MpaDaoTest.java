package ru.yandex.practicum.filmorate.dao.film;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class MpaDaoTest {

    private final MpaDao mpaStorage;

    @Test
    void findAllMpa() {
        List<Mpa> mpaList = mpaStorage.findAllMpa();
        assertEquals(5, mpaList.size());
    }

    @Test
    void findMpaById() {
        Mpa mpa = mpaStorage.findMpaById(1);
        assertEquals("G", mpa.getName());
    }
}