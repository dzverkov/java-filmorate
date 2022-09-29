package ru.yandex.practicum.filmorate.service.film;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.dao.film.MpaDao;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MpaServiceImpl implements MpaService {

    private final MpaDao mpaDao;


    @Override
    public List<Mpa> findAllMpa() {
        return mpaDao.findAllMpa();
    }

    @Override
    public Mpa findMpaById(int id) {
        return mpaDao.findMpaById(id);
    }
}
