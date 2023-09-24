package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class CompilationController {
    private final CompilationService compilationService;

    @GetMapping
    public ResponseEntity<List<CompilationDto>> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос compilations");
        return new ResponseEntity<>(compilationService.getCompilations(pinned, from, size), HttpStatus.OK);
    }

    @GetMapping("/{compId}")
    public ResponseEntity<CompilationDto> getCompilation(@Positive @PathVariable Long compId) {
        log.info("Получен GET-запрос compilation по id: {}", compId);
        return new ResponseEntity<>(compilationService.getCompilationById(compId), HttpStatus.OK);
    }
}