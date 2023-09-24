package ru.practicum.category.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.service.CategoryService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/admin/categories")
@RequiredArgsConstructor
@Slf4j
@Validated
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto newCatDto) {
        log.debug("Получен POST-запрс на создание категории: {}", newCatDto);
        return new ResponseEntity<>(categoryService.addCategory(newCatDto), HttpStatus.CREATED);
    }

    @DeleteMapping("/{catId}")
    public ResponseEntity<?> deleteCategoryById(@PathVariable @Positive Long catId) {
        log.debug("Получен DELETE-запрос на удаление категории по id: {}", catId);
        categoryService.deleteCategoryById(catId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(@PathVariable @Positive Long catId,
                                                      @Valid @RequestBody NewCategoryDto newCatDto) {
        log.debug("Получен PATCH-запрос на обновление категории: {}", newCatDto);
        return new ResponseEntity<>(categoryService.updateCategory(catId, newCatDto), HttpStatus.OK);
    }
}