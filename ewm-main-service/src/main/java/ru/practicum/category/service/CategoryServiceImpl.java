package ru.practicum.category.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.dto.CategoryDto;
import ru.practicum.category.dto.NewCategoryDto;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCatDto) {
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(
                categoryRepository.save(CategoryMapper.toCategory(newCatDto)));
        log.info("Категория {} успешно добавлена", newCatDto);
        return categoryDto;
    }

    @Override
    public void deleteCategoryById(Long catId) {
        try {
            categoryRepository.deleteById(catId);
            log.info("Категория с id {} успешно удалена", catId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Категория с id {} не найдена", catId);
            throw new EntityNotFoundException("Категория с id " + catId + " не найдена");
        }
    }

    @Override
    public CategoryDto updateCategory(Long catId,
                                      NewCategoryDto newCatDto) {
        if (!categoryRepository.existsById(catId)) {
            throw new EntityNotFoundException("Категория с id " + catId + " не найдена");
        }
        CategoryDto categoryDto = CategoryMapper.toCategoryDto(categoryRepository.save(
                new Category(catId, newCatDto.getName())));
        log.info("Категория с id {} успешно обновлена", catId);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<CategoryDto> categoryDtoList = categoryRepository
                .findAll(pageRequest)
                .map(a -> CategoryMapper.toCategoryDto(a))
                .toList();
        log.info("Получена категории с параметрами from {} и size {}", from, size);
        return categoryDtoList;
    }

    @Override
    public CategoryDto getCategoryById(Long catId) {
        CategoryDto categoryDto = CategoryMapper
                .toCategoryDto(categoryRepository.findById(catId)
                        .orElseThrow(() -> new EntityNotFoundException(
                                String.format("Категория с id " + catId + " успешно получена"))));
        log.info("Успешно получена категория по id {}", catId);
        return categoryDto;
    }
}