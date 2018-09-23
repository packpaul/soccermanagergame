package com.pp.toptal.soccermanager.exception;

/**
 * This business exception is intended to be used when querying for objects by
 * parameters (f.e. filtering, ordering etc).
 */
public class DataParameterException extends BusinessException {

    private static final long serialVersionUID = 1L;
    
    public DataParameterException(String message) {
        super(ErrorCode.INVALID_DATA, message);
    }

}
