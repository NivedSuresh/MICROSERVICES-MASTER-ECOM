package com.service.auth.exceptions;

public class PasswordMissMatchException extends UserException {
    public PasswordMissMatchException(String s, int value, String signupRequestPasswordMismatch) {
        super(s,value,signupRequestPasswordMismatch);

    }
}
