package ru.practicum.compilation.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
public class NewCompilationDto {
    private List<Long> events;
    private boolean pinned;
    @NotNull
    @Size(min = 1, max = 50)
    @NotBlank
    private String title;
}