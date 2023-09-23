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
import ru.practicum.event.dto.UpdateEventAdminRequest;
import ru.practicum.event.model.EventState;
import ru.practicum.event.service.EventService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
@RequestMapping("/admin/events")
public class AdminEventController {
    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<? extends EventShortDto>> getAllEvents(@RequestParam(required = false) List<Long> users,
                                                                      @RequestParam(required = false) List<EventState> states,
                                                                      @RequestParam(required = false) List<Long> categories,
                                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                      LocalDateTime rangeStart,
                                                                      @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                                      LocalDateTime rangeEnd,
                                                                      @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                      @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос, на получение всех events (admin)");
        return new ResponseEntity<>(eventService.getEvents(EventSearchParams.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build()), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable @Positive Long eventId,
                                                    @RequestBody @Valid UpdateEventAdminRequest request) {
        log.info("Получен PATCH-запрос, на обновление event (admin), id: {}, request: {}", eventId, request);
        return new ResponseEntity<>(eventService.updateEvent(eventId, request), HttpStatus.OK);
    }
}