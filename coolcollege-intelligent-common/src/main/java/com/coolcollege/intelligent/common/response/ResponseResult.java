package com.coolcollege.intelligent.common.response;

import com.coolcollege.intelligent.common.constant.Constants;
import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import org.slf4j.MDC;

import java.text.MessageFormat;

/**
 * 统一返回结果
 *
 * @author Aaron
 * @date 2019/12/20
 */
@Data
@ToString
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Result {
    private static final long serialVersionUID = -2217360460304088285L;

    public ResponseResult(int code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.requestId = MDC.get(Constants.REQUEST_ID);
    }

    public ResponseResult(int code, String message, T data, String stackTrace) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.stackTrace = stackTrace;
        this.requestId = MDC.get(Constants.REQUEST_ID);
    }

    public ResponseResult(int code, String message) {
        this.code = code;
        this.message = message;
        this.requestId = MDC.get(Constants.REQUEST_ID);
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
    private T data;

    /**
     * 异常堆栈信息
     */
    private String stackTrace;

    private String requestId;

    public long getSystemCurrentTime() {
        return System.currentTimeMillis();
    }

    public static<T> ResponseResult<T> success(T data) {
        return new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(), "操作成功", data);
    }

    public static ResponseResult success() {
        return new ResponseResult(ResponseCodeEnum.SUCCESS.getCode(), "操作成功");
    }

    public static ResponseResult fail(int code, String msg) {
        return new ResponseResult(code, msg, false);
    }

    public static ResponseResult fail(int code, String msg, String stackTrace) {
        return new ResponseResult(code, msg, false, stackTrace);
    }

    public static ResponseResult fail(ErrorCodeEnum responseEnum){
        return new ResponseResult(responseEnum.getCode(), responseEnum.getMessage(), false);
    }

    public static ResponseResult fail(ErrorCodeEnum responseEnum, Object... objects){
        String message = MessageFormat.format(responseEnum.getMessage(), objects);
        return new ResponseResult(responseEnum.getCode(), message, false);
    }

}
