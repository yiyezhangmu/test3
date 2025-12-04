package com.coolcollege.intelligent.facade.dto;

import java.io.Serializable;

public class BaseResultDTO<T extends Object> implements Serializable {

    private static final long serialVersionUID = -2217360460304088285L;

    private boolean success = true;
    /**
     * 返回码
     */
    private int resultCode;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 返回数据
     */
    private T data;

    public int getResultCode() {
        return resultCode;
    }

    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public BaseResultDTO() {
        super();
    }

    public BaseResultDTO(T data) {
        super();
        this.data = data;
    }

    public BaseResultDTO(int resultCode, String message, T data) {
        this.resultCode = resultCode;
        this.message = message;
        this.data = data;
    }

    public BaseResultDTO(int resultCode, String message) {
        this.resultCode = resultCode;
        this.message = message;
    }

    public static BaseResultDTO SuccessResult() {
        return new BaseResultDTO(ResultCodeDTO.SUCCESS.code, "请求成功");
    }

    public static BaseResultDTO SuccessResult(Object data) {
        return new BaseResultDTO(ResultCodeDTO.SUCCESS.code, "请求成功",data);
    }

    public static BaseResultDTO FailResult(String msg) {
        return new BaseResultDTO(ResultCodeDTO.FAIL.code, msg);
    }

    public static BaseResultDTO FailResult(int code, String msg) {
        return new BaseResultDTO(code, msg);
    }

}
