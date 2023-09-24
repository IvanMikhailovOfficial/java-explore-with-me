package ru.practicum.event.service;

import ru.practicum.event.dto.*;
import ru.practicum.request.dto.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

public interface EventService {

    EventFullDto addEvent(Long userId, NewEventDto eventDto);

    EventFullDto getEvent(Long userId, Long eventId);

    List<EventShortDto> getEvents(Long userId, Integer from, Integer size);

    EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request);

    List<ParticipationRequestDto> getRequests(Long userId, Long eventId);

    EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId, EventRequestStatusUpdateRequest request);

    EventFullDto getEvent(Long eventId, HttpServletRequest request);

    List<? extends EventShortDto> getEventsPublic(EventSearchParams params);

    List<? extends EventShortDto> getEvents(EventSearchParams params);

    EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request);
}