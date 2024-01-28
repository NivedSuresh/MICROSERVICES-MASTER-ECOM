package com.service.product.exception;

import lombok.Getter;

@Getter
public class ProductException extends RuntimeException{
    private final int statusCode;
    private final String errorCode;
    public ProductException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}
