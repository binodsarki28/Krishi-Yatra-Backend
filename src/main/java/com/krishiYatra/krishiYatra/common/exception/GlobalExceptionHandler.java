package com.krishiYatra.krishiYatra.common.exception;

import com.krishiYatra.krishiYatra.common.response.ServerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ServerResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        ServerResponse response = new ServerResponse("Validation Failed", HttpStatus.BAD_REQUEST.value(), HttpStatus.BAD_REQUEST, errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({org.springframework.security.authentication.BadCredentialsException.class,
            org.springframework.security.authentication.InternalAuthenticationServiceException.class})
    public ResponseEntity<ServerResponse> handleAuthenticationException() {
        ServerResponse response = ServerResponse.failureResponse(com.krishiYatra.krishiYatra.user.constant.UserConst.INVALID_CREDENTIALS, HttpStatus.UNAUTHORIZED);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ServerResponse> handleGeneralException(Exception ex) {
        ServerResponse response = ServerResponse.failureResponse(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
