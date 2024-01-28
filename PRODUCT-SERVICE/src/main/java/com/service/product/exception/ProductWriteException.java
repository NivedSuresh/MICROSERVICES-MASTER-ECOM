package com.service.product.exception;

import lombok.Getter;

@Getter
public class ProductWriteException extends ProductException {

    public ProductWriteException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
