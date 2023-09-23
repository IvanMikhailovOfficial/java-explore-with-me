package ru.practicum;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@Validated
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> addHit(@Valid @RequestBody EndpointHitDto hit) {
        log.debug("Получен  POST запрос на эндоинту /hit: {}", hit);
        return new ResponseEntity<>(service.addHit(hit), HttpStatus.OK);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStats>> getStats(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
                                                    LocalDateTime start,
                                                    @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                                    @RequestParam(required = false) String[] uris,
                                                    @RequestParam(defaultValue = "false") boolean unique) {
        log.debug("Запрос GET запрос на получение стат с {} до {}", start, end);
        return new ResponseEntity<>(service.getStats(start, end, uris, unique), HttpStatus.OK);
    }
}