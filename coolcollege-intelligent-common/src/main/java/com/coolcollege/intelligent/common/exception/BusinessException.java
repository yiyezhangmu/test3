package com.coolcollege.intelligent.common.exception;

import com.coolcollege.intelligent.common.enums.ErrorCodeEnum;
import com.coolcollege.intelligent.common.enums.ResponseCodeEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/07/20
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class BusinessException extends RuntimeException {
    /**
     * 返回码枚举类
     */
    private ErrorCodeEnum responseCodeEnum;

    /**
     * 构造函数
     */
    public BusinessException(ErrorCodeEnum responseCodeEnum) {
        this.responseCodeEnum = responseCodeEnum;
    }

    /**
     * 构造函数
     */
    public BusinessException(Throwable cause, ErrorCodeEnum responseCodeEnum) {
        super(cause);
        this.responseCodeEnum = responseCodeEnum;
    }
}
