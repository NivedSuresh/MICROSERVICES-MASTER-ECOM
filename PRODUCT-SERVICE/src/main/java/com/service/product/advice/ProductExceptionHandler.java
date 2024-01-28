package com.service.product.advice;

import com.service.product.exception.ProductException;
import com.service.product.payloads.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ProductExceptionHandler {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> productExceptionHandler(ProductException e){
        return ResponseEntity.status(e.getStatusCode())
                .body(ErrorResponse.builder()
                .code(e.getErrorCode())
                .message(e.getMessage()).build()
        );
    }

}
