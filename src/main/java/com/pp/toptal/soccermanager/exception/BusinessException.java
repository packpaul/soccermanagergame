package com.pp.toptal.soccermanager.exception;

/**
 * BusinessException extends RuntimeException and contains special field "errorCode".
 * It is converted to ErrorResponse object when it reaches the REST service.
 * @see com.pp.toptal.soccermanager.exception.ErrorResponse
 * @see ErrorCode
 */
public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public BusinessException(ErrorCode errorCode, Throwable cause) {
        super(cause);
        this.errorCode = errorCode;
    }
    
    public BusinessException(BusinessException source) {
        this(source.getErrorCode(), source.getMessage(), source.getCause());
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

}
