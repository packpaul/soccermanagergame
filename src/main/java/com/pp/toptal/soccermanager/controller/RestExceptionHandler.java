package com.pp.toptal.soccermanager.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.common.exceptions.InvalidGrantException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.pp.toptal.soccermanager.exception.BusinessException;
import com.pp.toptal.soccermanager.exception.ErrorCode;
import com.pp.toptal.soccermanager.exception.ErrorResponse;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<Object> handleException(BusinessException ex) {

        if (ex.getErrorCode() == null) {
            LOGGER.error(ex.getMessage(), ex);
        } else {
            LOGGER.error(ex.getMessage());
        }
        
        final HttpStatus status;
        switch(ex.getErrorCode()) {
            case OBJECT_NOT_FOUND:
            case VALIDATION_ERROR: {
                status = HttpStatus.NOT_ACCEPTABLE;
                break;
            }

            case INVALID_STATE:
            case INVALID_REASON: {
                status = HttpStatus.PRECONDITION_FAILED;
                break;
            }

            case INVALID_CREDENTIALS:
            case MISSING_CREDENTIALS:
            case DUPLICATE_CREDENTIALS: {
                status = HttpStatus.UNAUTHORIZED;
                break;
            }

            default: {
                status = HttpStatus.BAD_REQUEST;
            }
        }

        return ResponseEntity.status(status).body(new ErrorResponse(ex));
    }
    
    @ExceptionHandler(InvalidGrantException.class)
    public ResponseEntity<Object> handleException(InvalidGrantException ex) {
        LOGGER.error(ex.getMessage());

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                             .body(new ErrorResponse(ErrorCode.INVALID_CREDENTIALS, ex.getMessage()));
    }
    
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleException(Exception ex) {
        LOGGER.error(ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body(new ErrorResponse(ex));
    }

}