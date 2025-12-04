package com.coolcollege.intelligent.common.response;

import cn.hutool.core.util.ObjectUtil;
import lombok.Data;

/**
 * describe: 万店掌接口响应体
 *
 * @author wangff
 * @date 2024/10/16
 */
@Data
public class WdzResponse<T> {

    /**
     * 网关状态
     */
    private Stat stat;
    
    /**
     * 提示信息
     */
    private String result;
    
    /**
     * 业务信息
     */
    private T data;

    /**
     * 接口调用是否成功
     */
    public boolean isOk() {
        return ObjectUtil.isNotNull(this.stat) && "0".equals(this.stat.getCode());
    }


    @Data
    public static class Stat {

        /**
         * http请求的标识符
         */
        private String cid;

        /**
         * 网关返回码
         */
        private String code;

        /**
         * 网关返回码名称
         */
        private String codename;
        
        /**
         * 接口调用的当前时间
         */
        private String systime;
    }
}
