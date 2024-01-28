package com.service.order.exception;

import lombok.Getter;

@Getter
public class OrderException extends RuntimeException{
    int statusCode;
    String errorCode;
    public OrderException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}
