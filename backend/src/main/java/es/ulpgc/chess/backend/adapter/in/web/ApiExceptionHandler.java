package es.ulpgc.chess.backend.adapter.in.web;

import es.ulpgc.chess.backend.adapter.in.web.dto.ErrorResponse;
import es.ulpgc.chess.backend.application.GameNotFoundException;
import es.ulpgc.chess.backend.domain.InvalidMoveException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(GameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse notFound(GameNotFoundException e) {
        return new ErrorResponse("GAME_NOT_FOUND", e.getMessage());
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse badRequest(Exception e) {
        return new ErrorResponse("BAD_REQUEST", e.getMessage());
    }

    @ExceptionHandler({InvalidMoveException.class, IllegalStateException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(RuntimeException e) {
        return new ErrorResponse("CONFLICT", e.getMessage());
    }
}
