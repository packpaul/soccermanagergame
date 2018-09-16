package com.pp.toptal.soccermanager.exception;

/**
 * Error response that will be sent from REST services in case of any exception.
 * Contains "error" field of enumerated type ErrorCode.
 *
 * @see ErrorCode
 */
public class ErrorResponse {
    /**
     * NB! Field names are chosen to correspond to TokenEndpoint error response.
     * @see org.springframework.security.oauth2.provider.endpoint.TokenEndpoint
     * @see org.springframework.security.oauth2.common.exceptions.OAuth2ExceptionJackson2Serializer
     * If you want to change the names, configure Json serialization appropriately.
     */
    private ErrorCode error;
    private String error_description;

    public ErrorResponse() {
    }
    
    public ErrorResponse(ErrorCode error, String error_description) {
        this.error = error;
        this.error_description = error_description;
    }

    public ErrorResponse(BusinessException ex) {
        this(ex.getErrorCode(), ex.getMessage());
    }

    public ErrorResponse(Exception ex){
        this(ErrorCode.UNKNOWN_ERROR,
                ex.getMessage() == null || ex.getMessage().isEmpty() ? ex.getClass().getName() : ex.getMessage());
    }

    public ErrorCode getError() {
        return error;
    }

    public void setError(ErrorCode error) {
        this.error = error;
    }

    public String getError_description() {
        return error_description;
    }

    public void setError_description(String error_description) {
        this.error_description = error_description;
    }

}
