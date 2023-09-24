package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.ViewStats;
import ru.practicum.mapper.HitMapper;
import ru.practicum.repository.StatsRepository;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsServiceImpl implements StatsService {
    private final StatsRepository repository;

    @Override
    public EndpointHitDto addHit(EndpointHitDto dto) {
        log.info("EndpointHit успешно добавлен");
        return HitMapper.toEndpointHitDto(repository.save(HitMapper.toEndpointHit(dto)));
    }

    @Override
    public List<ViewStats> getStats(LocalDateTime start, LocalDateTime end, String[] uris, boolean unique) {
        if (start.isAfter(end)) throw new DateTimeException("Дата начала не может быть позже конца");
        if (unique) {
            if (uris != null) {
                log.info("статистика получена");
                return repository.getUniqueStats(start, end, uris);
            } else {
                return repository.getUniqueWithoutUris(start, end);
            }
        }
        if (uris == null) {
            log.info("статистика получена");
            return repository.getWithoutUris(start, end);
        }
        log.info("статистика получена");
        return repository.getStats(start, end, uris);
    }
}