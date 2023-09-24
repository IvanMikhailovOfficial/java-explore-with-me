package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityNotFoundException;
import java.time.DateTimeException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ExcHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleNotValidArg(final MethodArgumentNotValidException e) {
        log.info("Неправильно составлен запрос");
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleConstraintViolation(final DataIntegrityViolationException e) {
        log.info("Ограничение целостности нарушено");
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleBadQuery(final NumberFormatException e) {
        log.info("Неправильно составлен запрос");
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleNotFound(final EntityNotFoundException e) {
        log.info("Нужный объект не найден");
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleWrongDateTime(final DateTimeException e) {
        log.info("Для запрошенной операции условия не выполнены");
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleWrongStatus(final ConflictException e) {
        log.info("Для запрошенной операции условия не выполнены");
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler
    public ResponseEntity<ErrorEntity> handleRuntimeException(final RuntimeException e) {
        log.info("Что-то пошло не так");
        return new ResponseEntity<>(new ErrorEntity(e.getMessage(), LocalDateTime.now()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}