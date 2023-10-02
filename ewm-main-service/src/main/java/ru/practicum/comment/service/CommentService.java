package ru.practicum.comment.service;

import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentRequest;

import java.util.List;

public interface CommentService {
    CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto);

    CommentDto updateComment(Long userId, Long commentId, UpdateCommentRequest request);

    List<CommentDto> getCommentsByUserId(Long userId, Integer from, Integer size);

    List<CommentDto> getCommentsByEventId(Long eventId, Integer from, Integer size);

    CommentDto getCommentById(Long commentId);

    void deleteCommentById(Long commentId);
}
