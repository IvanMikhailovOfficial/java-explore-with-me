package ru.practicum.event.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.NonFinal;
import ru.practicum.event.model.EventState;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class EventSearchParams {
    private final List<Long> users;
    private final List<EventState> states;
    private final String text;
    private final List<Long> categories;
    private final Boolean paid;
    @NonFinal
    private LocalDateTime rangeStart;
    @NonFinal
    private LocalDateTime rangeEnd;
    private final Boolean onlyAvailable;
    private final String sort;
    private final Integer from;
    private final Integer size;
    private final HttpServletRequest request;
}