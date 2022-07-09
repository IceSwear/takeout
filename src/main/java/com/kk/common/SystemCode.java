package com.kk.common;


public enum SystemCode {

    STATUS_SUCCESS(0, "Success"),
    STATUS_FAILED(1, "Fail"),

    PARAMS_ERROR(1001, "Parameter Error"),
    SYSTEM_ERROR(1002, "System Error"),
    ACCESS_TOKEN_INVALID(1003, "Invalid Token");

    private int code;
    private String message;

    SystemCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}
