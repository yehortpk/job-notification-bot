package com.github.yehortpk.router.exception;

import com.github.yehortpk.router.models.response.APIResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException ex) {
        // Extract validation errors
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(new APIResponse(400, ex.getMessage()));
    }

    @ExceptionHandler({ProgressNotFoundException.class, ParserProgressNotFoundException.class, ParserPageProgressNotFoundException.class})
    public ResponseEntity<Object> handleProgressNotFoundException(RuntimeException ex) {
        return ResponseEntity.badRequest().body(new APIResponse(404, ex.getMessage()));
    }
}