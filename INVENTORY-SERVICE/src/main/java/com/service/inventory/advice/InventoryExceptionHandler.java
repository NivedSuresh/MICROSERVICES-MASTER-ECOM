package com.service.inventory.advice;

import com.service.inventory.exceptions.InventoryException;
import com.service.inventory.payloads.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(InventoryException.class)
    public ResponseEntity<ErrorResponse> handler(InventoryException e){
        return ResponseEntity.status(e.getStatus()).body(
                new ErrorResponse(e.getErrorCode(), e.getMessage())
        );
    }

}
