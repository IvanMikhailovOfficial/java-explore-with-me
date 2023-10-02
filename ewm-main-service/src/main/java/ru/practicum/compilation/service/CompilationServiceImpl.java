package ru.practicum.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.dto.UpdateCompilationRequest;
import ru.practicum.compilation.mapper.CompilationMapper;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.repository.CompilationRepository;
import ru.practicum.event.model.Event;
import ru.practicum.event.repository.EventRepository;

import javax.persistence.EntityNotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;

    @Override
    public CompilationDto addCompilation(NewCompilationDto compilationDto) {
        Set<Event> events = new HashSet<>();
        if (Objects.nonNull(compilationDto.getEvents())) {
            events = Set.copyOf(eventRepository.findAllById(compilationDto.getEvents()));
        }
        CompilationDto result = CompilationMapper.toCompilationDto(
                compilationRepository.save(CompilationMapper.toCompilation(compilationDto, events)));
        log.info("Compilation добавлен " + result);
        return result;
    }

    @Override
    public void deleteCompilation(Long compId) {
        try {
            compilationRepository.deleteById(compId);
        } catch (EmptyResultDataAccessException e) {
            throw new EntityNotFoundException("Compilation с id= " + compId + " не найден");
        }
    }

    @Override
    public CompilationDto updateCompilation(Long compId, UpdateCompilationRequest request) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Compilation c id= " + compId + " не найден"));

        if (Objects.nonNull(request.getPinned())) {
            compilation.setPinned(request.getPinned());
        }
        if (Objects.nonNull(request.getTitle())) {

            compilation.setTitle(request.getTitle());
        }
        if (Objects.nonNull(request.getEvents())) {
            compilation.setEvents(
                    new HashSet<>(eventRepository.findAllById(request.getEvents())));
        }
        CompilationDto result = CompilationMapper.toCompilationDto(compilationRepository.save(compilation));
        log.info("Обновление Compilation с id{} прошло успешно", compId);
        return result;
    }

    @Override
    public CompilationDto getCompilationById(Long compId) {
        CompilationDto result = CompilationMapper.toCompilationDto(compilationRepository.findById(compId).orElseThrow(() ->
                new EntityNotFoundException("Compilation с id= " + compId + " не найден")));
        log.info("Получение Compilation по id {}", compId);
        return result;
    }

    @Override
    public List<CompilationDto> getCompilations(Boolean pinned, Integer from, Integer size) {
        List<Compilation> compilations;
        if (Objects.nonNull(pinned)) {
            compilations = compilationRepository.findByPinned(pinned, PageRequest.of(from / size, size));
        } else {
            compilations = compilationRepository.findAll(PageRequest.of(from / size, size)).getContent();
        }
        List<CompilationDto> result = compilations.stream()
                .map(a -> CompilationMapper.toCompilationDto(a))
                .collect(Collectors.toList());
        log.info("Compilations получены с параметрами pinned ={},from={},size={}", pinned, from, size);
        return result;
    }
}