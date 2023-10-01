package ru.practicum.compilation.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.service.CompilationService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCompilationController {
    private final CompilationService compilationService;

    @PostMapping
    public ResponseEntity<CompilationDto> postCompilation(@Valid @RequestBody NewCompilationDto compilationDto) {
        log.info("Получен POST-запрос, на создание compilation (admin): {}", compilationDto);
        return new ResponseEntity<>(compilationService.addCompilation(compilationDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{compId}")
    public ResponseEntity<?> deleteCompilation(@Positive @PathVariable Long compId) {
        log.info("Получен DELETE-запрос, на удаление compilation по id: {}", compId);
        compilationService.deleteCompilation(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{compId}")
    public ResponseEntity<CompilationDto> patchCompilation(@Positive @PathVariable Long compId,
                                                           @RequestBody @Valid UpdateCompilationRequest compRequest) {
        log.info("Получен PATCH-запрос, на обновление compilation по id: {}", compId);
        return new ResponseEntity<>(compilationService.updateCompilation(compId, compRequest), HttpStatus.OK);
    }
}