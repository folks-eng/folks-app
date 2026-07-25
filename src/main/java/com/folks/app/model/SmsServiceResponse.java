package com.folks.app.model;

import java.io.Serializable;


/**
 * This class has the
 *
 * @author schan280
 */
public class SmsServiceResponse implements Serializable, Cloneable {

    private String httpStatusCode;

    private String errorCode;

    private String errorMsg;

    public SmsServiceResponse() {}

    public String getHttpStatusCode() {
        return httpStatusCode;
    }

    public void setHttpStatusCode(String httpStatusCode) {
        this.httpStatusCode = httpStatusCode;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }


    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }
}