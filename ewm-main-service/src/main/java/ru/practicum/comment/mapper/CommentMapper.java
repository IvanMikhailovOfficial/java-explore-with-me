package ru.practicum.comment.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.comment.dto.CommentDto;
import ru.practicum.comment.dto.NewCommentDto;
import ru.practicum.comment.model.Comment;
import ru.practicum.event.model.Event;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;

@UtilityClass
public class CommentMapper {

    public Comment toComment(NewCommentDto newCommentDto, User user, Event event) {
        return Comment.builder()
                .created(LocalDateTime.now())
                .text(newCommentDto.getText())
                .event(event)
                .author(user)
                .build();
    }

    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .author(comment.getAuthor().getName())
                .text(comment.getText())
                .event(comment.getEvent().getId())
                .id(comment.getId())
                .created(comment.getCreated())
                .build();
    }
}