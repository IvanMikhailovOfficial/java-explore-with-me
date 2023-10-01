package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventSearchParams;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.service.EventService;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@Slf4j
@RequiredArgsConstructor
@Validated
public class EventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<? extends EventShortDto>> getEvents(@RequestParam(required = false) String text,
                                                                   @RequestParam(required = false) List<Long> categories,
                                                                   @RequestParam(required = false) Boolean paid,
                                                                   @RequestParam(required = false)
                                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                   LocalDateTime rangeStart,
                                                                   @RequestParam(required = false)
                                                                   @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                   LocalDateTime rangeEnd,
                                                                   @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                                                   @RequestParam(required = false) String sort,
                                                                   @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                   @Positive @RequestParam(defaultValue = "10") Integer size,
                                                                   HttpServletRequest httpServletRequest) {
        log.info("Получен GET-запрос events (public)");
        return new ResponseEntity<>(eventService.getEventsPublic(
                EventSearchParams.builder()
                        .text(text)
                        .categories(categories)
                        .paid(paid)
                        .rangeStart(rangeStart)
                        .rangeEnd(rangeEnd)
                        .onlyAvailable(onlyAvailable)
                        .sort(sort)
                        .from(from)
                        .size(size)
                        .request(httpServletRequest)
                        .build()), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EventFullDto> getEventById(@Positive @PathVariable Long id, HttpServletRequest httpServletRequest) {
        log.info("Получен GET-запрос event (public) по id: {}", id);
        return new ResponseEntity<>(eventService.getEvent(id, httpServletRequest), HttpStatus.OK);
    }
}