package com.service.order.exception;

public class LackOfInformationFromDbException extends OrderException {
    public LackOfInformationFromDbException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
