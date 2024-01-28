package com.service.inventory.exceptions;

import lombok.Getter;

@Getter
public class InventoryException extends RuntimeException{
    String errorCode;
    int status;
    public InventoryException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }
}
