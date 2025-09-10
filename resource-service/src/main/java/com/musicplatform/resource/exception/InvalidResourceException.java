package com.musicplatform.resource.exception;

public class InvalidResourceException extends RuntimeException {

    public InvalidResourceException(String message) {
        super(message);
    }

    public InvalidResourceException(String message, Throwable cause) {
        super(message, cause);
    }
}
