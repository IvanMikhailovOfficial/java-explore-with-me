package ru.practicum.comment.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@RequiredArgsConstructor(onConstructor = @__(@JsonCreator(mode = JsonCreator.Mode.PROPERTIES)))
@Data
public class UpdateCommentRequest {
    @NotNull
    @Size(min = 3, max = 3000)
    private final String text;
}