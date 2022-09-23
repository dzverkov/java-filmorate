package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.model.Mpa;
import ru.yandex.practicum.filmorate.service.film.MpaService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mpa")
public class MpaController {

    private final MpaService mpaService;


    @GetMapping
    public List<Mpa> findAllGenres() {
        return mpaService.findAllMpa();
    }

    @GetMapping(value = "/{id}")
    public Mpa findMpaById(@PathVariable int id) {
        return mpaService.findMpaById(id);
    }
}
