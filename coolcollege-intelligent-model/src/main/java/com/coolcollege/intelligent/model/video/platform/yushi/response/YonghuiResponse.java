package com.coolcollege.intelligent.model.video.platform.yushi.response;

import lombok.Data;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2021/03/24
 */
@Data
public class YonghuiResponse<T> {
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


}
