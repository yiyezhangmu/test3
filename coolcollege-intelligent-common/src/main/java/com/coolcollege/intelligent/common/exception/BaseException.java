package com.coolcollege.intelligent.common.exception;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Description 统一异常类返回
 * @author Aaron
 * @date 2019/12/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BaseException extends RuntimeException {

    /**
     * 返回码枚举类
     */
   private ErrorCodeEnum responseCodeEnum;

    /**
     * 构造函数
     */
    public BaseException(ErrorCodeEnum responseCodeEnum) {
        this.responseCodeEnum = responseCodeEnum;
    }



    /**
     * 构造函数
     */
    public BaseException(Throwable cause, ErrorCodeEnum responseCodeEnum) {
        super(cause);
        this.responseCodeEnum = responseCodeEnum;
    }

}
