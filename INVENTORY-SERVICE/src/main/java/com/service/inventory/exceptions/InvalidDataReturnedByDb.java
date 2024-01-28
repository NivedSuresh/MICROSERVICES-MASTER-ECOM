package com.service.inventory.exceptions;

public class InvalidDataReturnedByDb extends InventoryException {
    public InvalidDataReturnedByDb(String message, String errorCode, int statusCode) {
        super(message, errorCode, statusCode);
    }
}
