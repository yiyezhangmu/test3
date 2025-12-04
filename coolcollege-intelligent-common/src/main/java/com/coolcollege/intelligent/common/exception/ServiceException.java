package com.coolcollege.intelligent.common.exception;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import org.apache.poi.ss.formula.functions.T;

import lombok.Data;

import java.text.MessageFormat;

/**
 * @Description 业务异常类返回
 * @author Aaron
 * @date 2019/12/20
 */
@Data
public class ServiceException extends RuntimeException{
    private static final long serialVersionUID = -5068776742356414959L;

    /**
     * 返回码
     */
    private  Integer errorCode;

    /**
     * 返回信息
     */
    private  String errorMessage;

    private T data;

    /**
     * 构造函数
     * @param errorCode
     * @param errorMessage
     */
    @Deprecated
    public ServiceException(Integer errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }


    /**
     * 构造函数
     * @param errorCode
     * @param errorMessage
     */
    @Deprecated
    public ServiceException(Integer errorCode, String errorMessage, T data) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
        this.data = data;
    }

    /**
     * 构造函数
     * @param errorMessage
     */
    @Deprecated
    public ServiceException(String errorMessage) {
        super(errorMessage);
        this.errorMessage = errorMessage;
    }

    /**
     * 构造函数
     * @param errorCode
     * @param errorMessage
     * @param cause
     */
    @Deprecated
    public ServiceException(Integer errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public ServiceException(ErrorCodeEnum responseEnum) {
        super(responseEnum.getMessage());
        this.errorCode = responseEnum.getCode();
        this.errorMessage = responseEnum.getMessage();
    }

    public ServiceException(ErrorCodeEnum responseEnum, Object... objects) {
        super(responseEnum.getMessage());
        String message = MessageFormat.format(responseEnum.getMessage(), objects);
        this.errorCode = responseEnum.getCode();
        this.errorMessage = message;
    }



}
