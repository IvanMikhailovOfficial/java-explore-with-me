package ru.practicum.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.StatsController;

import java.time.DateTimeException;

@RestControllerAdvice(basePackageClasses = StatsController.class)
public class ExcHandler {

    @ExceptionHandler(DateTimeException.class)
    public ResponseEntity<ErrorEntity> handleDateTimeExc(final DateTimeException e) {
        return new ResponseEntity<>(new ErrorEntity(e.getMessage()), HttpStatus.BAD_REQUEST);
    }
}