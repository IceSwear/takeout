package com.kk.exception;

import com.kk.common.SystemCode;
import lombok.Data;

@Data
public class AppException extends RuntimeException {

    private int errCode;
    private String message;
    private Object attachment;

    public AppException(SystemCode systemCode) {
        this.errCode = systemCode.getCode();
        this.message = systemCode.getMessage();
    }

    public AppException(int errCode, String message) {
        super(message);
        this.errCode = errCode;
        this.message = message;
    }

    public AppException(int errCode, String message, Object attachment) {
        super(message);
        this.errCode = errCode;
        this.message = message;
        this.attachment = attachment;
    }

    public AppException(int errCode, String message, Throwable ex) {
        super(message, ex);
        this.errCode = errCode;
        this.message = message;
    }

}
