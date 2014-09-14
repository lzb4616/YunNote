package com.app.linyu.client;


import com.app.linyu.json.JSONObject;

/**
 * 跟有道云API有关的异常
 *
 * @author haiwen
 */
public class AppException extends Exception {
    protected static final String ERROR = "error";
    protected static final String MESSAGE = "message";

    private static final long serialVersionUID = 1L;

    protected int errorCode;

    public AppException(JSONObject json) {
        super(json.getString(AppException.MESSAGE));
        this.errorCode = json.getInt(AppException.ERROR);
    }

    public AppException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AppException(int errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public AppException(Throwable cause) {
        super(cause);
    }

    public int getErrorCode() {
        return errorCode;
    }
}

