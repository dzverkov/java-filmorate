package ru.yandex.practicum.filmorate.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    private int id;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = ".*\\S.*")
    private String login;
    private String name;

    @Past
    private LocalDate birthday;

}
