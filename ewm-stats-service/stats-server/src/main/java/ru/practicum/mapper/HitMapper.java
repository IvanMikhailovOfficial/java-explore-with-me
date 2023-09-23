package ru.practicum.mapper;

import ru.practicum.EndpointHitDto;
import ru.practicum.model.EndpointHit;

public class HitMapper {

    public static EndpointHit toEndpointHit(EndpointHitDto dto) {
        return EndpointHit.builder()
                .timestamp(dto.getTimestamp())
                .uri(dto.getUri())
                .app(dto.getApp())
                .ip(dto.getIp())
                .build();
    }

    public static EndpointHitDto toEndpointHitDto(EndpointHit hit) {
        return EndpointHitDto.builder()
                .timestamp(hit.getTimestamp())
                .uri(hit.getUri())
                .app(hit.getApp())
                .ip(hit.getIp())
                .build();
    }
}