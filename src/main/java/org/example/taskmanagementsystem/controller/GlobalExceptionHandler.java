package org.example.taskmanagementsystem.controller;

import org.example.taskmanagementsystem.dto.ErrorResponse;
import org.example.taskmanagementsystem.exception.AccessDeniedException;
import org.example.taskmanagementsystem.exception.DuplicateEmailException;
import org.example.taskmanagementsystem.exception.ResourceNotFoundException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
       ErrorResponse error = new ErrorResponse(404, ex.getMessage());
       return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DuplicateEmailException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateEmail(DuplicateEmailException ex) {
        ErrorResponse error = new ErrorResponse(403, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        System.out.println(ex.getMessage());
        ErrorResponse error = new ErrorResponse(403, "Data integrity violation");
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler (BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials
            (BadCredentialsException ex) {
        System.out.println(ex.getMessage());
        ErrorResponse error = new ErrorResponse(401, "Invalid email or password");
        return new ResponseEntity<>(error, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler (AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied
            (AccessDeniedException ex) {
        ErrorResponse error = new ErrorResponse(403, ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.FORBIDDEN);
    }


    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleRuntimeError(RuntimeException ex) {
        System.out.println(ex.getMessage());
        ex.printStackTrace();
        ErrorResponse error = new ErrorResponse(500, "Internal Server Error");
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
