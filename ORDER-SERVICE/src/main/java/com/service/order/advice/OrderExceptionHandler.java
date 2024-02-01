package com.service.order.advice;

import com.service.order.exception.OrderException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
@Slf4j
public class OrderExceptionHandler {

    @ExceptionHandler(OrderException.class)
    public ResponseEntity<ErrorResponse> orderException(OrderException e){
        log.error("OrderExceptionHandler triggered.");
        return ResponseEntity.status(e.getStatusCode()).body(
                ErrorResponse.builder()
                .code(e.getErrorCode())
                .message(e.getMessage())
                .build()
        );
    }

}
