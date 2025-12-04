package com.coolcollege.intelligent.model.platform;

import lombok.Data;

import java.io.Serializable;

/**
 * PlatformApi 酷学院PlatformApi返回的最新版结果峰状类
 *
 * @author 柳敏 min.liu@coolcollege.cn
 * @since 2021-05-13 11:10
 */
@Data
public class PlatformApiResponse<T> implements Serializable {

    private static final long serialVersionUID = -8555651329355841072L;

    /**
     * 0表示接口调用成功
     */
    private int code = 0;

    private String msg;

    private T data;

}
