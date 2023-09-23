package ru.practicum.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.service.RequestService;

import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/{userId}/requests")
@Slf4j
@Validated
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getAllRequests(@PathVariable @Positive Long userId) {
        log.info("Получен GET-запрос, на получение requests от user по id: {}", userId);
        return new ResponseEntity<>(requestService.getRequests(userId), HttpStatus.OK);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ParticipationRequestDto> postRequest(@PathVariable @Positive Long userId,
                                                               @RequestParam @Positive Long eventId) {
        log.info("Получен POST-запрос, на создание request по userId: {} и eventId: {}", userId, eventId);
        return new ResponseEntity<>(requestService.addRequest(userId, eventId), HttpStatus.CREATED);
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> updateRequest(@PathVariable @Positive Long userId,
                                                                 @PathVariable @Positive Long requestId) {
        log.info("Получен PATCH-запрос, для user request по userId: {} и requestId: {}", userId, requestId);
        return new ResponseEntity<>(requestService.updateRequest(userId, requestId), HttpStatus.OK);
    }
}