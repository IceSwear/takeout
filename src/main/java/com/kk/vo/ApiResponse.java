package com.kk.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.kk.common.Constants;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(value = JsonInclude.Include.NON_NULL)
@Setter
@Getter
public class ApiResponse<T> {

    /**
     * 0=successful ,others(except 0) are defeat
     */
    private int status;
    private String message;
    private T data;

    public ApiResponse() {
        status = Constants.SUCCESS;
        message = "success";
    }

    public ApiResponse(T data) {
        status = Constants.SUCCESS;
        message = "success";
        this.data = data;
    }

    public static ApiResponse fail(String message) {
        return new ApiResponse(Constants.FAIL, message, null);
    }

    public static ApiResponse fail(int status, String message) {
        return new ApiResponse(status, message, null);
    }


    public static ApiResponse success(Object object) {
        return new ApiResponse(object);
    }

    public static ApiResponse success() {
        return new ApiResponse();
    }

    public ApiResponse(int status, String message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }


}
