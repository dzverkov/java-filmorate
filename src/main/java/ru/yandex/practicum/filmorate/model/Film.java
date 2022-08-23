package ru.yandex.practicum.filmorate.model;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class Film {
    private int id;

    @NotBlank
    private String name;

    @NotBlank
    @Size(max = 200)
    private String description;

    private LocalDate releaseDate;

    @Positive
    private int duration;

    private final Set<Integer> likes = new HashSet<>();

    public boolean addLike(int userId) {
        return likes.add(userId);
    }

    public boolean deleteLike(int userId) {
        return likes.remove(userId);
    }
}
