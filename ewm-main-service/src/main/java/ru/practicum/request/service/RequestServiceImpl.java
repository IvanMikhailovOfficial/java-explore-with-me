package ru.practicum.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.event.repository.EventRepository;
import ru.practicum.request.dto.ParticipationRequestDto;
import ru.practicum.request.dto.RequestStatus;
import ru.practicum.request.mapper.RequestMapper;
import ru.practicum.request.model.ParticipationRequest;
import ru.practicum.request.repository.RequestRepository;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public ParticipationRequestDto addRequest(Long userId, Long eventId) {

        Event event = eventRepository.findById(eventId).orElseThrow(() ->
                new EntityNotFoundException("Event с id " + eventId + " не найден"));

        User user = userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("User с id " + userId + " не найден"));

        if (requestRepository.findByRequester_IdAndEvent_Id(userId, eventId).isPresent()) {
            throw new DataIntegrityViolationException("Такого ParticipationRequest не существует");
        }

        if (userId.equals(event.getInitiator().getId())) {
            throw new DataIntegrityViolationException("Initiator не может быть принят");
        }


        if (!EventState.PUBLISHED.equals(event.getState())) {
            throw new DataIntegrityViolationException("Event не опубликован");
        }

        if (event.getParticipantLimit() != 0 &&
                event.getParticipantLimit() <= requestRepository.countByEvent_IdAndStatus(eventId, RequestStatus.CONFIRMED)) {
            throw new DataIntegrityViolationException("Достигнут лимит участников");
        }

        ParticipationRequestDto result = RequestMapper.toRequestDto(requestRepository.save(RequestMapper.toRequest(user, event,
                !event.getRequestModeration() || event.getParticipantLimit() == 0 ?
                        RequestStatus.CONFIRMED : RequestStatus.PENDING)));
        log.info("Успешно добавлен request с id {} и с eventId {}", userId, eventId);
        return result;
    }

    @Override
    public ParticipationRequestDto updateRequest(Long userId, Long requestId) {
        ParticipationRequest request = requestRepository.findByRequester_IdAndId(userId, requestId).orElseThrow(() ->
                new EntityNotFoundException("Запроса с  id " + requestId + " не существует"));
        request.setStatus(RequestStatus.CANCELED);
        requestRepository.save(request);
        ParticipationRequestDto result = RequestMapper.toRequestDto(request);
        log.info("Успено получены запросы от юзера с id {} и с requestId {}", userId, requestId);
        return result;
    }


    @Override
    public List<ParticipationRequestDto> getRequests(Long userId) {
        userRepository.findById(userId).orElseThrow(() ->
                new EntityNotFoundException("Юзер с id " + userId + " не найден"));
        List<ParticipationRequestDto> result = requestRepository.findByRequester_Id(userId).stream()
                .map(a->RequestMapper.toRequestDto(a))
                .collect(Collectors.toList());
        log.info("Успено получены запросы от юзера с id {}", userId);
        return result;
    }
}