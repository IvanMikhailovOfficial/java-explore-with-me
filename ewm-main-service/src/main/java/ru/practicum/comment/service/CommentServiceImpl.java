package ru.practicum.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.comment.repository.CommentRepository;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentRequest;
import ru.practicum.comment.mapper.CommentMapper;
import ru.practicum.comment.model.Comment;

import ru.practicum.event.model.Event;
import ru.practicum.event.model.EventState;

import ru.practicum.event.repository.EventRepository;
import ru.practicum.exception.EditingProhibitedException;
import ru.practicum.exception.WrongEventStatusException;
import ru.practicum.user.model.User;
import ru.practicum.user.repository.UserRepository;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        User currentUser = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "User с id " + userId + " не существует"));
        Event currentEvent = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                "Event с id " + eventId + " не существует"));

        if (!EventState.PUBLISHED.equals(currentEvent.getState())) {
            throw new WrongEventStatusException("Event not published");
        }
        CommentDto result = CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(newCommentDto, currentUser, currentEvent)));
        log.info("Comment от userId {} к eventId {} успешно добавлен", userId, eventId);
        return result;
    }

    @Override
    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "User с id " + userId + " не существует"));
        Comment currentComment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException(
                "Comment с id " + commentId + " не существует"));

        if (!userId.equals(currentComment.getAuthor().getId())) {
            throw new EditingProhibitedException(
                    "Только автор может обновить комментарий");
        }

        currentComment.setText(request.getText());
        CommentDto result = CommentMapper.toCommentDto(commentRepository.save(currentComment));
        log.info("Comment c id {} от userId {} успешно обовлен", commentId, userId);
        return result;
    }

    @Override
    public List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
                "User с id " + userId + " не существует"));
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<CommentDto> result = commentRepository.findByAuthor_Id(userId, pageRequest).stream()
                .map(a -> CommentMapper.toCommentDto(a)).collect(Collectors.toList());
        log.info("Comments с параметрами userId {}, from {}, size {} успешно получены", userId, from, size);
        return result;
    }

    @Override
    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
        eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
                "Event с id " + eventId + " не существует"));
        PageRequest pageRequest = PageRequest.of(from / size, size);
        List<CommentDto> result = commentRepository.findByEvent_Id(eventId, pageRequest).stream()
                .map(a -> CommentMapper.toCommentDto(a)).collect(Collectors.toList());
        log.info("Comments с параметрами eventId {}, from {}, size {} успешно получены", eventId, from, size);
        return result;
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        CommentDto result = CommentMapper.toCommentDto(commentRepository.findById(commentId).orElseThrow(() ->
                new EntityNotFoundException("Comment с id " + commentId + " не существует")));
        log.info("Comment успешно получен по id {}", commentId);
        return result;
    }

    @Override
    public void deleteCommentById(Long commentId) {
        try {
            commentRepository.deleteById(commentId);
            log.info("Comment успешно удален по id {}", commentId);
        } catch (EmptyResultDataAccessException e) {
            log.info("Comment с id {} не существует", commentId);
            throw new EntityNotFoundException("Comment с id " + commentId + " не существует");
        }
    }
}

//    @Override
//    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
//        User user = userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
//                "User с id " + userId + " не существует"));
//        Event event = eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
//                "Event с id " + eventId + " не существует"));
//
//        if (!EventState.PUBLISHED.equals(event.getState())) throw new WrongEventStatusException("Event not published");
//        log.info("Comment от userId {} к eventId {} успешно добавлен", userId, eventId);
//        return CommentMapper.toCommentDto(commentRepository.save(CommentMapper.toComment(newCommentDto, user, event)));
//    }
//
//    @Override
//    public CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest request) {
//        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
//                "User с id " + userId + " не существует"));
//        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new EntityNotFoundException(
//                "Comment с id " + commentId + " не существует"));
//
//        if (!userId.equals(comment.getAuthor().getId())) throw new EditingProhibitedException(
//                "Только автор может обновить комментарий");
//
//        comment.setText(request.getText());
//        log.info("Comment c id {} от userId {} успешно обовлен", commentId, userId);
//        return CommentMapper.toCommentDto(commentRepository.save(comment));
//    }
//
//    @Override
//    public List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size) {
//        userRepository.findById(userId).orElseThrow(() -> new EntityNotFoundException(
//                "User с id " + userId + " не существует"));
//        log.info("Comments с параметрами userId {}, from {}, size {} успешно получены", userId, from, size);
//        return commentRepository.findByAuthor_Id(userId, PageRequest.of(from / size, size)).stream()
//                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size) {
//        eventRepository.findById(eventId).orElseThrow(() -> new EntityNotFoundException(
//                "Event с id " + eventId + " не существует"));
//        log.info("Comments с параметрами eventId {}, from {}, size {} успешно получены", eventId, from, size);
//        return commentRepository.findByEvent_Id(eventId, PageRequest.of(from / size, size)).stream()
//                .map(CommentMapper::toCommentDto).collect(Collectors.toList());
//    }
//
//    @Override
//    public CommentDto getCommentById(Long commentId) {
//        log.info("Comment успешно получен по id {}", commentId);
//        return CommentMapper.toCommentDto(commentRepository.findById(commentId).orElseThrow(() ->
//                new EntityNotFoundException(
//                        "Comment с id " + commentId + " не существует")));
//    }
//
//    @Override
//    public void deleteCommentById(Long commentId) {
//        try {
//            commentRepository.deleteById(commentId);
//            log.info("Comment успешно удален по id {}", commentId);
//        } catch (EmptyResultDataAccessException e) {
//            log.info("Comment с id {} не существует", commentId);
//            throw new EntityNotFoundException("Comment с id " + commentId + " не существует");
//        }
//    }
//}