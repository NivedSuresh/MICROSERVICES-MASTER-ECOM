package com.service.order.exception;

public class UnableToPlaceOrderException extends OrderException {
    public UnableToPlaceOrderException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
