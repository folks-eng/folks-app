package com.folks.app.exception;

import java.util.UUID;

public class DataValidationException extends RuntimeException {

    private String errorCode;
    private String message;

    public DataValidationException(String msg) {
        super(msg);
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String s) {
        this.message = s;
    }

}
