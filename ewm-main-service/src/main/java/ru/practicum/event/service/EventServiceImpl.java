package ru.practicum.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.EndpointHitDto;
import ru.practicum.StatsClient;
import ru.practicum.ViewStats;
import ru.practicum.category.model.Category;
import ru.practicum.category.repository.CategoryRepository;
import ru.practicum.event.dto.*;
import ru.practicum.event.mapper.EventMapper;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.ConflictException;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import javax.servlet.http.HttpServletRequest;
import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public EventFullDto addEvent(Long userId, NewEventDto eventDto) {
        if (eventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new DateTimeException(
                    "Поле: дата события. Ошибка: до мероприятия меньше двух часов. " + eventDto.getEventDate());
        }
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User с id" + userId + " не найден"));
        Category category = categoryRepository.findById(eventDto.getCategory()).orElseThrow(() ->
                new EntityNotFoundException("Category с id " + eventDto.getCategory() + " не найден"));
        EventFullDto result = EventMapper.toEventFullDto(eventRepository.save(EventMapper.toEvent(eventDto, user, category)));
        log.info("Event успешео добавлен");
        return result;
    }

    @Override
    public EventFullDto getEvent(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User c id " + userId + " не найден"));

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event c id " + eventId + " не найден"));
        EventFullDto result = setStats(event);
        log.info("Event получен по userId {} eventId {}", userId, eventId);
        return result;
    }

    @Override
    public List<EventShortDto> getEvents(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User с id " + userId + " не найден"));
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventRepository.findByInitiator(user, pageable);
        List<EventShortDto> result = events.stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());
        log.info("Events получены по userId {} from {} size {}", userId, from, size);
        return result;
    }

    @Override
    public EventFullDto updateEvent(Long userId, Long eventId, UpdateEventUserRequest request) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User с id " + userId + " не найден"));

        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() ->
                new EntityNotFoundException("Event с id " + eventId + " не найден"));

        if (EventState.PUBLISHED == event.getState()) {
            throw new ConflictException(
                    "Изменить можно только ожидающие или отмененные события.");
        }

        if (request.getStateAction() != null) {
            event.setState(request.getStateAction() == StateAction.SEND_TO_REVIEW ?
                    EventState.PENDING : EventState.CANCELED);
        }
        EventFullDto result = update(event, request, false);
        log.info("Event  c eventId {} userId {} обновлен", eventId, userId);
        return result;
    }

    @Override
    public List<ParticipationRequestDto> getRequests(Long userId, Long eventId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User с id " + userId + " не найден"));

        eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() ->
                new EntityNotFoundException("Event с id " + eventId + " не найден"));
        List<ParticipationRequestDto> result = requestRepository.findByEvent_Id(eventId).stream()
                .map(RequestMapper::toRequestDto)
                .collect(Collectors.toList());
        log.info("getRequests c параметрами userId {} eventId {} успешнов выполен", userId, eventId);
        return result;
    }

    @Override
    public EventRequestStatusUpdateResult updateRequest(Long userId, Long eventId,
                                                        EventRequestStatusUpdateRequest request) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User с id " + userId + " не найден"));

        Event event = eventRepository.findByIdAndInitiator_Id(eventId, userId).orElseThrow(() ->
                new EntityNotFoundException("Event с id " + eventId + " не найден"));

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            throw new ConflictException(
                    "Модерация не требуется");
        }

        long slots = event.getParticipantLimit() - requestRepository.countByEvent_IdAndStatus(eventId,
                RequestStatus.CONFIRMED);
        if (slots <= 0) {
            throw new ConflictException("Достигнут лимит участников");
        }

        List<ParticipationRequest> requests = requestRepository.findAllById(request.getRequestIds());

        for (ParticipationRequest req : requests) {
            if (request.getStatus() == RequestStatus.CONFIRMED && slots > 0) {
                req.setStatus(RequestStatus.CONFIRMED);
                slots--;
            } else if (request.getStatus() == RequestStatus.REJECTED) {
                req.setStatus(RequestStatus.REJECTED);
            }
        }
        requestRepository.saveAll(requests);
        EventRequestStatusUpdateResult result = EventRequestStatusUpdateResult.builder()
                .confirmedRequests(requestRepository.findByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED).stream()
                        .map(RequestMapper::toRequestDto).collect(Collectors.toList()))
                .rejectedRequests(requestRepository.findByEvent_IdAndStatus(eventId, RequestStatus.REJECTED).stream()
                        .map(RequestMapper::toRequestDto).collect(Collectors.toList())).build();
        log.info("Обновление Request по userId {} eventId {} прошло успешно", userId, eventId);
        return result;
    }

    @Override
    public EventFullDto getEvent(Long eventId, HttpServletRequest httpServletRequest) {
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event c id " + eventId + " не найден"));

        if (currentEvent.getState() != EventState.PUBLISHED) {
            throw new EntityNotFoundException("Event с id " + eventId + "не найден");
        }

        saveHit(httpServletRequest);
        EventFullDto result = setStats(currentEvent);
        log.info("получен Event с параметрами eventId {} ", eventId);
        return result;
    }

    public List<? extends EventShortDto> getEventsPublic(EventSearchParams params) {
        if (params.getRangeStart() == null) {
            params.setRangeStart(LocalDateTime.now());
        }
        if (params.getRangeEnd() == null) {
            params.setRangeEnd(LocalDateTime.now().plusYears(100));
        }

        if (params.getRangeStart().isAfter(params.getRangeEnd())) {
            throw new DateTimeException("Конец не может быть раньше начала");
        }

        List<EventShortDto> searchedEvents = eventRepository.search(
                        params.getText(), params.getCategories(),
                        params.getPaid(), params.getRangeStart(),
                        params.getRangeEnd(), params.getOnlyAvailable(),
                        PageRequest.of(params.getFrom() / params.getSize(), params.getSize()))
                .stream().map(EventMapper::toEventShortDto).collect(Collectors.toList());

        boolean sortBool = false;
        if (params.getSort() != null) {
            if (params.getSort().equals("EVENT_DATE")) {
                searchedEvents = searchedEvents.stream()
                        .sorted(Comparator.comparing((a -> a.getEventDate())))
                        .collect(Collectors.toList());
            } else sortBool = true;
        }

        saveHit(params.getRequest());
        List<? extends EventShortDto> result = sortBool ? setStats(searchedEvents, params.getRangeStart(), params.getRangeEnd()).stream()
                .sorted(Comparator.comparingLong(EventShortDto::getViews))
                .collect(Collectors.toList()) : setStats(searchedEvents, params.getRangeStart(), params.getRangeEnd());
        log.info("получены Events по парметрам " + params);
        return result;
    }

    @Override
    public List<? extends EventShortDto> getEvents(EventSearchParams params) {
        if (params.getRangeStart() == null) {
            params.setRangeStart(LocalDateTime.now());
        }
        if (params.getRangeEnd() == null) {
            params.setRangeEnd(LocalDateTime.now().plusYears(100));
        }

        List<EventFullDto> events = eventRepository.adminSearch(
                        params.getUsers(), params.getStates(), params.getCategories(),
                        params.getRangeStart(), params.getRangeEnd(), PageRequest.of(params.getFrom() / params.getSize(), params.getSize()))
                .stream().map(a -> EventMapper.toEventFullDto(a)).collect(Collectors.toList());
        List<? extends EventShortDto> result = setStats(events, params.getRangeStart(), params.getRangeEnd());
        log.info("Получены Events по параметрам {}", params);
        return result;
    }

    @Override
    public EventFullDto updateEvent(Long eventId, UpdateEventAdminRequest request) {
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event с id " + eventId + " не найден"));

        if (request.getStateAction() != null) {
            if (currentEvent.getState() == EventState.PUBLISHED || currentEvent.getState() == EventState.CANCELED) {
                throw new ConflictException("Уже опубликовано или отменено");
            }
            if (request.getStateAction() == AdminStateAction.PUBLISH_EVENT) {
                currentEvent.setState(EventState.PUBLISHED);
                currentEvent.setPublishedOn(LocalDateTime.now());
            } else {
                currentEvent.setState(EventState.CANCELED);
            }
        }
        EventFullDto result = update(currentEvent, request, true);
        log.info("Event с id {} обновлен", eventId);
        return result;
    }

    private EventFullDto update(Event event, UpdateEvent request, boolean isAdmin) {
        if (request.getEventDate() != null) {
            if (request.getEventDate().isBefore(LocalDateTime.now().plusHours(isAdmin ? 1 : 2))) {
                throw new DateTimeException("Ошибка: событие слишком скорое " + request.getEventDate());
            }
            event.setEventDate(request.getEventDate());
        }

        if (!isAdmin) if (request.getLocation() != null) {
            event.setLocation(request.getLocation());
        }

        if (request.getAnnotation() != null) {
            event.setAnnotation(request.getAnnotation());
        }
        if (request.getCategory() != null) {
            event.setCategory(categoryRepository.findById(request.getCategory())
                    .orElseThrow(() -> new EntityNotFoundException("Category c id " + request.getCategory() + " не найдена")));
        }
        if (request.getDescription() != null) {
            event.setDescription(request.getDescription());
        }
        if (request.getPaid() != null) {
            event.setPaid(request.getPaid());
        }
        if (request.getParticipantLimit() != null) {
            event.setParticipantLimit(request.getParticipantLimit());
        }
        if (request.getRequestModeration() != null) {
            event.setRequestModeration(request.getRequestModeration());
        }
        if (request.getTitle() != null) {
            event.setTitle(request.getTitle());
        }

        EventFullDto result = EventMapper.toEventFullDto(eventRepository.save(event));
        log.info("update {} успешно выполнен", event);
        return result;
    }

    private void saveHit(HttpServletRequest httpServletRequest) {
        statsClient.postHit(EndpointHitDto.builder()
                .app("ewm-main-service")
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteAddr())
                .timestamp(LocalDateTime.now())
                .build());
    }

    private EventFullDto setStats(Event event) {
        List<ViewStats> stats = statsClient.getStats(
                event.getCreatedOn().format(FORMATTER),
                LocalDateTime.now().format(FORMATTER),
                new String[]{String.format("/events/%d", event.getId())},
                true);

        EventFullDto dto = EventMapper.toEventFullDto(event);
        if (stats.size() == 0) {
            dto.setViews(0L);
        } else {
            dto.setViews(stats.get(0).getHits());
        }
        return dto;
    }

    private List<? extends EventShortDto> setStats(List<? extends EventShortDto> events,
                                                   LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd) {
        Map<Long, ? extends EventShortDto> dtoMap = events.stream()
                .collect(Collectors.toMap(a -> a.getId(), Function.identity()));
        Map<String, Long> uris = new HashMap<>();

        for (Long aLong : dtoMap.keySet()) {
            uris.put(String.format("/events/%d", aLong), aLong);
        }

        List<ViewStats> stats = statsClient.getStats(
                rangeStart.format(FORMATTER),
                rangeEnd.format(FORMATTER),
                uris.keySet().toArray(new String[0]),
                true);

        for (ViewStats stat : stats) {
            if (uris.containsKey(stat.getUri())) {
                dtoMap.get(uris.get(stat.getUri())).setViews((stat.getHits()));
            }
        }
        return new ArrayList<>(dtoMap.values());
    }
}