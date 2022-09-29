package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class Mpa {

    @NotBlank
    private int id;

    @NotBlank
    @Size(max = 50)
    private String name;
}
