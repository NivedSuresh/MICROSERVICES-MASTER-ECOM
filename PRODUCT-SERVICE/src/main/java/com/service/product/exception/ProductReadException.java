package com.service.product.exception;

import lombok.Getter;

@Getter
public class ProductReadException extends ProductException {
    public ProductReadException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
