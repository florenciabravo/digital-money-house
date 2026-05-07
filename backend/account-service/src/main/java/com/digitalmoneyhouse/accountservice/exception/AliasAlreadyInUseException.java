package com.digitalmoneyhouse.accountservice.exception;

public class AliasAlreadyInUseException extends RuntimeException {
    public AliasAlreadyInUseException(String message) {
        super(message);
    }
}
