package com.levi.demo.api.exception;

import com.levi.demo.api.dto.ErrorResponse;
import com.levi.demo.application.exception.ApplicationException;
import com.levi.demo.domain.exception.DomainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> handleDomainException(DomainException exception) {
        log.error("Domain error appears: {}", exception.getMessage());
        return ResponseEntity.badRequest().body(
                new ErrorResponse("BUSINESS_ERROR", exception.getMessage(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(ApplicationException.class)
    public ResponseEntity<ErrorResponse> handleApplicationException(ApplicationException exception) {
        log.error("Application error appears: {}", exception.getMessage());
        return ResponseEntity.badRequest().body(
                new ErrorResponse("BUSINESS_ERROR", exception.getMessage(), LocalDateTime.now())
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedException(Exception exception) {
        log.error("Unexpected error appears", exception);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResponse("INTERNAL_ERROR", exception.getMessage(), LocalDateTime.now())
        );
    }
}
