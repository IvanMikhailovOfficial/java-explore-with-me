package ru.practicum.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.dto.UpdateCommentRequest;
import ru.practicum.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@RestController
@RequestMapping("/users/{userId}/comments")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping("/{eventId}")
    public ResponseEntity<CommentDto> addComment(@PathVariable @Positive Long userId,
                                                 @PathVariable @Positive Long eventId,
                                                 @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info("Получен POST-запрос на добавление comment с параметрами: user id {}, event id {}", userId, eventId);
        return new ResponseEntity<>(commentService.addComment(userId, eventId, newCommentDto), HttpStatus.CREATED);
    }

    @PatchMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(@PathVariable @Positive Long userId,
                                                    @PathVariable @Positive Long commentId,
                                                    @Valid @RequestBody UpdateCommentRequest request) {
        log.info("Получен PATCH-запрос, на обновление comment с параметрами: user id {}, comment id {}", userId, commentId);
        return new ResponseEntity<>(commentService.updateComment(userId, commentId, request), HttpStatus.OK);
    }
}