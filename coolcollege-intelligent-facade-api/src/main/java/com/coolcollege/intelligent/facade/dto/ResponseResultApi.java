package com.coolcollege.intelligent.facade.dto;

import java.io.Serializable;

/**
 * 统一返回结果
 *
 * @author Aaron
 * @date 2019/12/20
 */
public class ResponseResultApi<T> implements Serializable {

    private static final long serialVersionUID = -2217360460304088285L;

    public ResponseResultApi(int code, String message, T data, boolean isSuccess, String requestId) {
        this.code = code;
        this.message = message;
        this.bizData = data;
        this.isSuccess = isSuccess;
        this.requestId = requestId;
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

    public T getBizData() {
        return bizData;
    }

    public void setBizData(T bizData) {
        this.bizData = bizData;
    }

    public Boolean getSuccess() {
        return isSuccess;
    }

    public void setSuccess(Boolean success) {
        isSuccess = success;
    }

    public String getStackTrace() {
        return stackTrace;
    }

    public void setStackTrace(String stackTrace) {
        this.stackTrace = stackTrace;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /**
     * 返回码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T bizData;

    /**
     * 是否成功
     */
    private Boolean isSuccess;

    /**
     * 异常堆栈信息
     */
    private String stackTrace;

    /**
     * 请求id
     */
    private String requestId;

    public long getSystemCurrentTime() {
        return System.currentTimeMillis();
    }

    public static<T> ResponseResultApi<T> success(T data, String requestId) {
        return new ResponseResultApi(200, "操作成功", data, Boolean.TRUE, requestId);
    }

    public static ResponseResultApi fail(int code, String msg, String requestId) {
        return new ResponseResultApi(code, msg, null, Boolean.FALSE, requestId);
    }

    @Override
    public String toString() {
        return "{" +
                "\"code\":" + code +
                ", \"message\":\"" + message + '\"' +
                ", \"isSuccess\":" + isSuccess +
                ", \"requestId\":\"" + requestId + '\"' +
                ", \"bizData\":" + bizData +
                '}';
    }
}
