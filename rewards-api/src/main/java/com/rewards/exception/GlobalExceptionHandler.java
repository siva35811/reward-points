package com.rewards.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private Map<String, Object> buildResponse(HttpStatus status, String message) {
        Map<String, Object> body = new HashMap<>( );
        body.put( "status", status.value( ) );
        body.put( "error", status.getReasonPhrase( ) );
        body.put( "message", message );
        body.put( "timestamp", LocalDateTime.now( ).toString( ) );
        return body;
    }

    @ExceptionHandler({DateTimeParseException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String, Object>> handleBadRequest(Exception ex) {
        return ResponseEntity
                .status( HttpStatus.BAD_REQUEST )
                .body( buildResponse( HttpStatus.BAD_REQUEST, ex.getMessage( ) ) );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArg(IllegalArgumentException ex) {
        return ResponseEntity
                .status( HttpStatus.UNPROCESSABLE_ENTITY )
                .body( buildResponse( HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage( ) ) );
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<Map<String, Object>> handleNotFound(NoSuchElementException ex) {
        return ResponseEntity
                .status( HttpStatus.NOT_FOUND )
                .body( buildResponse( HttpStatus.NOT_FOUND, ex.getMessage( ) ) );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleAll(Exception ex) {
        return ResponseEntity
                .status( HttpStatus.INTERNAL_SERVER_ERROR )
                .body( buildResponse( HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage( ) ) );
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, Object>> handleConflict(IllegalStateException ex) {
        return ResponseEntity
                .status( HttpStatus.CONFLICT )
                .body( buildResponse( HttpStatus.CONFLICT, ex.getMessage( ) ) );
    }


}
