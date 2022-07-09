package com.kk.spring.advice;

import com.kk.common.Constants;
import com.kk.common.SystemCode;
import com.kk.exception.AppException;
import com.kk.vo.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;

@ControllerAdvice
public class ExceptionAdvice {

    private static Logger logger = LoggerFactory.getLogger(ExceptionAdvice.class);

    @Autowired
    private HttpServletRequest httpServletRequest;

    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public Object errorHandler(Exception ex) {
        ApiResponse response = new ApiResponse();
        if (ex instanceof AppException) {
            AppException amEx = (AppException) ex;
            response.setStatus(Constants.FAIL);
            response.setMessage(amEx.getMessage());
            return response;
        }
        if (ex instanceof MissingServletRequestParameterException
                || ex instanceof HttpMessageNotReadableException
                || ex instanceof HttpMediaTypeNotAcceptableException) {
            //miss request params
            response.setStatus(Constants.FAIL);
            response.setMessage(ex.getMessage());

        } else if (ex instanceof ConstraintViolationException) {
            ConstraintViolationException sc = (ConstraintViolationException) ex;
            response.setStatus(Constants.FAIL);
            response.setMessage(sc.getMessage());
        } else {
            response.setStatus(Constants.FAIL);
            response.setMessage("服务器繁忙，请稍后再试!");
            logger.error("Exception error", ex);
        }
        return response;
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public ApiResponse httpMethodNotSupportedErrorHandler(HttpRequestMethodNotSupportedException opEx) {
        logger.error("HttpRequestMethodNotSupportedException error {} url:{}", opEx, httpServletRequest.getRequestURI());

        return ApiResponse.fail(SystemCode.SYSTEM_ERROR.getCode(), opEx.getMessage());
    }

    @ExceptionHandler(value = {MethodArgumentTypeMismatchException.class})
    @ResponseBody
    public ApiResponse methodArgumentTypeMismatchException(MethodArgumentTypeMismatchException exception) {
        exception.printStackTrace();
        logger.error("url:{}",httpServletRequest.getRequestURI());
        return ApiResponse.fail(SystemCode.SYSTEM_ERROR.getCode(), exception.getMessage());
    }

}
