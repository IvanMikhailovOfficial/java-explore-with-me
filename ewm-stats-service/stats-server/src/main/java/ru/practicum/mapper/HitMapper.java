package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

@UtilityClass
public class HitMapper {

    public EndpointHit toEndpointHit(EndpointHitDto dto) {
        return EndpointHit.builder()
                .timestamp(dto.getTimestamp())
                .uri(dto.getUri())
                .app(dto.getApp())
                .ip(dto.getIp())
                .build();
    }

    public EndpointHitDto toEndpointHitDto(EndpointHit hit) {
        return EndpointHitDto.builder()
                .timestamp(hit.getTimestamp())
                .uri(hit.getUri())
                .app(hit.getApp())
                .ip(hit.getIp())
                .build();
    }
}