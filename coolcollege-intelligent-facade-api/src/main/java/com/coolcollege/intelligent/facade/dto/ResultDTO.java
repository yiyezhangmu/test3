package com.coolcollege.intelligent.facade.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;

public class ResultDTO<T extends Object> implements Serializable {

    private static final long serialVersionUID = -2217360460304088285L;

    private boolean success = true;
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
    private T data;

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResultDTO() {
        super();
    }

    public ResultDTO(T data) {
        super();
        this.data = data;
    }

    public ResultDTO(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public ResultDTO(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public static ResultDTO SuccessResult() {
        return new ResultDTO(ResultCodeDTO.SUCCESS.code, "请求成功");
    }

    public static ResultDTO SuccessResult(Object data) {
        return new ResultDTO(ResultCodeDTO.SUCCESS.code, "请求成功",data);
    }

    public static ResultDTO FailResult(String msg) {
        return new ResultDTO(ResultCodeDTO.FAIL.code, msg);
    }

    public static ResultDTO FailResult(int code, String msg) {
        return new ResultDTO(code, msg);
    }

}
