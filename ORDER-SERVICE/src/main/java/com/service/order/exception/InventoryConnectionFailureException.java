package com.service.order.exception;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class InventoryConnectionFailureException extends OrderException{
    public InventoryConnectionFailureException(String message, int statusCode, String errorCode) {
        super(message, statusCode, errorCode);
    }
}
