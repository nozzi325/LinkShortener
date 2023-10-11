package by.zhukovsky.LinkShortener.controller;

import by.zhukovsky.LinkShortener.dto.ErrorResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.time.LocalDateTime;

@RestControllerAdvice
public class ExceptionControllerAdvice {
    Logger logger = LoggerFactory.getLogger(ExceptionControllerAdvice.class);

    @ExceptionHandler({EntityExistsException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse> exceptionEntityExistsHandler(Exception e) {
        logger.error("Exception occurred: " + e.getMessage());
        ErrorResponse error = createErrorResponse(e.getMessage());
        HttpStatus status = (e instanceof EntityExistsException) ? HttpStatus.BAD_REQUEST : HttpStatus.NOT_FOUND;
        return ResponseEntity
                .status(status)
                .body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> exceptionMethodArgumentNotValidHandler(MethodArgumentNotValidException e) {
        StringBuilder errorMessage = new StringBuilder();
        e.getBindingResult().getFieldErrors()
                .forEach(fieldError -> errorMessage.append(fieldError.getDefaultMessage()).append("; "));
        ErrorResponse errorDetails = createErrorResponse(String.valueOf(errorMessage).trim());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(errorDetails);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception e) {
        logger.error("Exception occurred: " + e.getMessage(), e);
        ErrorResponse error = createErrorResponse("An unexpected error occurred.");
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(error);
    }

    private ErrorResponse createErrorResponse(String message) {
        return new ErrorResponse(message, LocalDateTime.now());
    }
}
