package com.coolcollege.intelligent.common.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @Description 统一返回结果
 * @author Aaron
 * @date 2019/12/20
 */
@Data
@AllArgsConstructor
@ToString
@Builder
public class ResponseResultMessage implements Result {
    private static final long serialVersionUID = -2217360460304088285L;

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回信息
     */
    private String message;

    /**
     * 全局requestId
     */
    private String requestId;

    public ResponseResultMessage(){}

}
