package com.hogar.seguro.exception;

//ex. ResidentId not found
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
