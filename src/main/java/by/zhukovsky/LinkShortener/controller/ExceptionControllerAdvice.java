package by.zhukovsky.LinkShortener.controller;

import by.zhukovsky.LinkShortener.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> exceptionEntityExistsHandler(EntityExistsException e) {
        logger.error(e.getMessage());
        ErrorResponse error = createErrorResponse(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionEntityNotFoundHandler(EntityNotFoundException e) {
        logger.error(e.getMessage());
        ErrorResponse error = createErrorResponse(e.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> exceptionMethodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        StringBuilder errMessage = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errMessage.append(String.format("%s; ", fieldError.getDefaultMessage()));
        }
        ErrorResponse errorDetails = createErrorResponse(String.valueOf(errMessage).trim());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDetails);
    }

    private ErrorResponse createErrorResponse(String message) {
        return new ErrorResponse(message, LocalDateTime.now());
    }
}
