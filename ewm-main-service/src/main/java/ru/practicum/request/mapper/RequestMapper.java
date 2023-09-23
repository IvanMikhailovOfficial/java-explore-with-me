package ru.practicum.request.mapper;

import ru.practicum.event.model.Event;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestStatus;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class RequestMapper {

    private RequestMapper() {
    }

    public static ParticipationRequestDto toRequestDto(ParticipationRequest request) {
        return ParticipationRequestDto.builder()
                .created(request.getCreated().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .id(request.getId())
                .event(request.getEvent().getId())
                .status(request.getStatus().toString())
                .requester(request.getRequester().getId())
                .build();
    }

    public static ParticipationRequest toRequest(User requester, Event event, RequestStatus status) {
        return ParticipationRequest.builder()
                .requester(requester)
                .status(status)
                .created(LocalDateTime.now())
                .event(event)
                .build();
    }
}