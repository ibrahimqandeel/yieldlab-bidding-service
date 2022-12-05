package com.yieldlab.bidding.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

@RestControllerAdvice
public class AppExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<String> handleAppException(ResponseStatusException responseStatusException){
        return new ResponseEntity<>(responseStatusException.getReason(),
                responseStatusException.getStatus());
    }
}
