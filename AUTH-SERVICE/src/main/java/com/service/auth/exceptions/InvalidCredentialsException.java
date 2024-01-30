package com.service.auth.exceptions;

public class InvalidCredentialsException extends UserException {

    public InvalidCredentialsException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
