package com.service.auth.exceptions;

public class UserAlreadyExistsException extends UserException {
    public UserAlreadyExistsException(String message, int httpStatus, String errorCode) {
        super(message, httpStatus, errorCode);
    }
}
