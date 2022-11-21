package by.zhukovsky.LinkShortener.controller;

import by.zhukovsky.LinkShortener.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    Logger logger = LoggerFactory.getLogger(LinkController.class);

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ErrorResponse> exceptionEntityExistsHandler(EntityExistsException e) {
        logger.error(e.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(error);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> exceptionEntityNotFoundHandler(EntityNotFoundException e) {
        logger.error(e.getMessage());
        ErrorResponse error = new ErrorResponse();
        error.setMessage(e.getMessage());
        return ResponseEntity
                .notFound()
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> exceptionMethodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        ErrorResponse errorDetails = new ErrorResponse();
        StringBuilder errMessage = new StringBuilder();
        for (FieldError fieldError : e.getBindingResult().getFieldErrors()) {
            errMessage.append(String.format("%s; ",fieldError.getDefaultMessage()));
        }
        errorDetails.setMessage(String.valueOf(errMessage).trim());
        return ResponseEntity
                .badRequest()
                .body(errorDetails);
    }
}
