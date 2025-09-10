package com.musicplatform.song.exception;

public class DuplicateMetadataException extends RuntimeException {

    public DuplicateMetadataException(String message) {
        super(message);
    }

    public  DuplicateMetadataException(String message, Throwable cause) {
        super(message, cause);
    }
}
