package ru.practicum.event.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.category.mapper.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.dto.EventFullDto;
import ru.practicum.event.dto.EventShortDto;
import ru.practicum.event.dto.NewEventDto;
import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;
import ru.practicum.user.mapper.UserMapper;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class EventMapper {
    public EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .eventDate(event.getEventDate())
                .location(event.getLocation())
                .participantLimit(event.getParticipantLimit())
                .paid(event.getPaid())
                .publishedOn(event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .title(event.getTitle())
                .state(event.getState().toString())
                .confirmedRequests(event.getConfirmedRequests())
                .build();
    }

    public Event toEvent(NewEventDto eventDto, User user, Category category) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .location(eventDto.getLocation())
                .category(category)
                .description(eventDto.getDescription())
                .paid(eventDto.isPaid())
                .initiator(user)
                .participantLimit(eventDto.getParticipantLimit())
                .eventDate(eventDto.getEventDate())
                .requestModeration(eventDto.getRequestModeration())
                .title(eventDto.getTitle())
                .createdOn(LocalDateTime.now())
                .state(EventState.PENDING)
                .build();
    }

    public EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .annotation(event.getAnnotation())
                .category(CategoryMapper.toCategoryDto(event.getCategory()))
                .paid(event.getPaid())
                .confirmedRequests(event.getConfirmedRequests())
                .id(event.getId())
                .initiator(UserMapper.toUserShortDto(event.getInitiator()))
                .title(event.getTitle())
                .eventDate(event.getEventDate())
                .build();
    }
}