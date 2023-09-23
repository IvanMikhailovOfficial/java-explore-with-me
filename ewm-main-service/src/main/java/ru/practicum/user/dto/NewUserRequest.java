package ru.practicum.user.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@AllArgsConstructor
@Data
@Builder
public class NewUserRequest {
    @NotNull
    @Email
    @Size(min = 6, max = 254)
    private String email;
    private Long id;
    @NotNull
    @NotBlank
    @Size(min = 2, max = 250)
    private String name;
}