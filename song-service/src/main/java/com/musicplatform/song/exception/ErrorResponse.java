package com.musicplatform.song.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {
    private final String errorMessage;
    private final String errorCode;
    private final Map<String, String> details;

    public ErrorResponse(String errorMessage, String errorCode) {
        this(errorMessage, errorCode, null);
    }

    public ErrorResponse(String errorMessage, String errorCode, Map<String, String> details) {
        this.errorMessage = errorMessage;
        this.errorCode = errorCode;
        this.details = details;
    }

    public String getErrorMessage() { return errorMessage; }
    public String getErrorCode() { return errorCode; }
    public Map<String, String> getDetails() { return details; }
}
