package com.coolcollege.intelligent.common.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

/**
 * @Author suzhuhong
 * @Date 2022/7/12 9:58
 * @Version 1.0
 */
@Data
@ToString
@Builder
@JsonIgnoreProperties(
        ignoreUnknown = true
)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class OpenApiFailResult implements Result{

    /**
     * 返回码
     */
    private int code;

    /**
     * 返回信息
     */
    private String message;


    public OpenApiFailResult(int code, String message) {
        this.code = code;
        this.message = message;
    }



}
