package com.coolcollege.intelligent.model.log;

import lombok.Data;

import java.util.Date;

/**
 * Description for this class
 *
 * @author : jixiang.jiang
 * @version : 1.0
 * @Description : Description for this class
 * @date ：Created in 2021/1/7 14:35
 */
@Data
public class OperationLogDO {

    /**
     * 操作类型
     */
    private String operateModule;
    /**
     * 操作模块
     */
    private String operateType;
    /**
     * 操作描述
     */
    private String operateDesc;
    /**
     * 操作方法
     */
    private String operateMethod;
    /**
     * 入参
     */
    private String requestParam;
    /**
     * 出参
     */
    private String responseParam;
    /**
     * 用户id
     */
    private String userId;
    /**
     * 用户名
     */
    private String userName;
    /**
     * ip
     */
    private String ip;
    /**
     * url
     */
    private String url;
    /**
     * 创建时间
     */
    private Date createTime;
}
