package com.choodon.rpc.base.exception;

import java.io.Serializable;

/**
 * @author cqq 2017.03.07
 * @version V1.0
 */

public class ErrorMsg implements Serializable {

    private static final long serialVersionUID = -3718308440254544982L;
    private int status;
    private int errorcode;
    private String message;

    public ErrorMsg(int status, int errorcode, String message) {
        this.status = status;
        this.errorcode = errorcode;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public int getErrorCode() {
        return errorcode;
    }

    public String getMessage() {
        return message;
    }

}