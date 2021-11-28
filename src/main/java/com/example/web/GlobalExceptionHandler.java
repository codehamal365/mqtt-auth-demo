package com.example.web;

import com.example.dto.ResponseDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static java.util.stream.Collectors.joining;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = Exception.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO exceptionHandler(Exception e) {
        log.error("exception {} occurs,error message {}", e, e.getMessage());
        return ResponseDTO.error(e.getMessage());
    }

    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public ResponseDTO validateHandler(MethodArgumentNotValidException e) {
        log.error("exception {} occurs,error message {}", e, e.getMessage());
        var message = e.getFieldErrors().stream().
                map(DefaultMessageSourceResolvable::getDefaultMessage)
                .collect(joining(","));
        return ResponseDTO.error(message);
    }
}
