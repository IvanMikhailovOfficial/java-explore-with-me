package ru.practicum.compilation.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.dto.CompilationDto;
import ru.practicum.compilation.dto.NewCompilationDto;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;

import java.util.Set;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {


    public Compilation toCompilation(NewCompilationDto compilationDto,
                                     Set<Event> events) {
        return Compilation.builder()
                .title(compilationDto.getTitle())
                .pinned(compilationDto.isPinned())
                .events(events)
                .build();
    }

    public CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .events(compilation.getEvents().stream()
                        .map(EventMapper::toEventShortDto)
                        .collect(Collectors.toList()))
                .build();
    }
}