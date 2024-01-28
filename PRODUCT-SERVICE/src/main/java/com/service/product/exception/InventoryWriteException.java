package com.service.product.exception;

public class InventoryWriteException extends ProductException{
    public InventoryWriteException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
