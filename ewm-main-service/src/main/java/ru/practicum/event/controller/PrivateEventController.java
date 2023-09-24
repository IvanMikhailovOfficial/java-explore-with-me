package ru.practicum.event.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.event.dto.*;
import ru.practicum.event.service.EventService;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping("/users/{userId}/events")
@RequiredArgsConstructor
@Slf4j
@Validated
public class PrivateEventController {
    private final EventService eventService;

    @PostMapping
    public ResponseEntity<EventFullDto> postEvent(@PathVariable @Positive Long userId,
                                                  @Valid @RequestBody NewEventDto eventDto) {
        log.info("Получен POST-запрос, от user {} на создание event: {}", userId, eventDto);
        return new ResponseEntity<>(eventService.addEvent(userId, eventDto), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getAllEvents(@PathVariable @Positive Long userId,
                                                            @RequestParam(defaultValue = "0") Integer from,
                                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос, на получение events от user с id: {}", userId);
        return new ResponseEntity<>(eventService.getEvents(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getEventById(@PathVariable @Positive Long userId,
                                                     @PathVariable @Positive Long eventId) {
        log.info("Получен GET-запрос, на получение event по id: {}", eventId);
        return new ResponseEntity<>(eventService.getEvent(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEvent(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long eventId,
                                                    @Valid @RequestBody UpdateEventUserRequest request) {
        log.info("Получен PATCH-запрос, на обновление event (user): {}", request);
        return new ResponseEntity<>(eventService.updateEvent(userId, eventId, request), HttpStatus.OK);
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsById(@PathVariable @Positive Long userId,
                                                                         @PathVariable @Positive Long eventId) {
        log.info("Получен GET-запрос, на получение requests, user: {}, event: {}", userId, eventId);
        return new ResponseEntity<>(eventService.getRequests(userId, eventId), HttpStatus.OK);
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> updateRequest(@PathVariable @Positive Long userId,
                                                                        @PathVariable @Positive Long eventId,
                                                                        @RequestBody @Valid EventRequestStatusUpdateRequest request) {
        log.info("Получен PATCH-запрос, на обновление requests, user: {}, event: {}, request: {}", userId, eventId, request);
        return new ResponseEntity<>(eventService.updateRequest(userId, eventId, request), HttpStatus.OK);
    }
}