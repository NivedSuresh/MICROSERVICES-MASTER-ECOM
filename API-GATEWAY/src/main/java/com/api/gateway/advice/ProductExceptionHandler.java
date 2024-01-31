package com.api.gateway.advice;

import com.api.gateway.exceptions.AuthorizationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductExceptionHandler {
    @ExceptionHandler
    public ResponseEntity<ErrorResponse> productExceptionHandler(AuthorizationException e){
        return ResponseEntity.status(e.getStatusCode())
                .body(ErrorResponse.builder()
                .errorCode(e.getErrorCode())
                .message(e.getMessage()).build()
        );
    }

}
