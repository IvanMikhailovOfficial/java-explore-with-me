package ru.practicum.event.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.event.model.Location;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
public class NewEventDto {
    @NotNull
    @Size(min = 20, max = 2000)
    private final String annotation;
    @NotNull
    @PositiveOrZero
    private final Long category;
    @NotNull
    @Size(min = 20, max = 7000)
    private final String description;
    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private final LocalDateTime eventDate;
    @NotNull
    @Valid
    private final Location location;
    private final boolean paid;
    @PositiveOrZero
    private final int participantLimit;
    private final Boolean requestModeration = true;
    @NotNull
    @Size(min = 3, max = 120)
    private final String title;
}