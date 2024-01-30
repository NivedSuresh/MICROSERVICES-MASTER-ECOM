package com.service.auth.advice;

import com.service.auth.exceptions.ErrorResponse;
import com.service.auth.exceptions.UserException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class UserExceptionHandler {

    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponse> userExceptionHandler(UserException e){
        return ResponseEntity.status(e.getStatusCode())
                .body(ErrorResponse.builder().errorCode(e.getErrorCode())
                        .message(e.getMessage()).build());
    }
}

