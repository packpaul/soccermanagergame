package com.pp.toptal.soccermanager.exception;

import com.fasterxml.jackson.annotation.JsonValue;

public enum ErrorCode {

    INVALID_CREDENTIALS("invalid_credentials"),
    MISSING_CREDENTIALS("missing_credentials"),
    DUPLICATE_CREDENTIALS("duplicate_credentials"),
    
    VALIDATION_ERROR("validation_error"),
    OBJECT_NOT_FOUND("object_not_found"),

    INVALID_STATE("invalid_state"),
    INVALID_REASON("invalid_reason"),
    INVALID_DATA("invalid_data"),

    UNKNOWN_ERROR("unknown_error");
    
    private final String errorCode;

    ErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    @JsonValue
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return errorCode;
    }

}
