package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.service.CommentService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping("/admin/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCommentController {
    private final CommentService commentService;

    @GetMapping("/events/{eventId}")
    public ResponseEntity<List<CommentDto>> getCommentsByEventId(@Positive @PathVariable Long eventId,
                                                                 @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                 @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос, на получение комментариев по event с id {}", eventId);
        return new ResponseEntity<>(commentService.getCommentsByEventId(eventId, from, size), HttpStatus.OK);
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<CommentDto>> getCommentsByUserId(@Positive @PathVariable Long userId,
                                                                @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                                                @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Получен GET-запрос, на получение комментов user по id {}", userId);
        return new ResponseEntity<>(commentService.getCommentsByUserId(userId, from, size), HttpStatus.OK);
    }

    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getCommentById(@Positive @PathVariable Long commentId) {
        log.info("Получен GET-запрос на получение коммента по id {}", commentId);
        return new ResponseEntity<>(commentService.getCommentById(commentId), HttpStatus.OK);
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<?> deleteCommentById(@Positive @PathVariable Long commentId) {
        log.info("Получен DELETE-запрос, на удаление по id {}", commentId);
        commentService.deleteCommentById(commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}