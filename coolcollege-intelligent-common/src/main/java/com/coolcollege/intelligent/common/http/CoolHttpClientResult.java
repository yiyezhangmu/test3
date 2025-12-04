package com.coolcollege.intelligent.common.http;


import java.io.Serializable;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/06/04
 */
public class CoolHttpClientResult implements Serializable {

    private static final long serialVersionUID = 2168152194164783950L;

    /**
     * 响应状态码
     */
    private int code;

    /**
     * 响应数据
     */
    private String content;

    public CoolHttpClientResult() {
    }

    public CoolHttpClientResult(int code) {
        this.code = code;
    }

    public CoolHttpClientResult(String content) {
        this.content = content;
    }

    public CoolHttpClientResult(int code, String content) {
        this.code = code;
        this.content = content;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "CoolHttpClientResult [code=" + code + ", content=" + content + "]";
    }

}
