package com.service.auth.exceptions;

import lombok.Data;
import lombok.Getter;

@Getter
public class UserException extends RuntimeException{
    private final int statusCode;
    private final String errorCode;

    public UserException(String message, int statusCode, String errorCode) {
        super(message);
        this.statusCode = statusCode;
        this.errorCode = errorCode;
    }
}
