package com.service.order.exception;

public class OutOfStockException extends OrderException {
    public OutOfStockException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
