package com.coolcollege.intelligent.facade.dto;

import lombok.Data;


/**
 * @author byd
 */
@Data
public class SyncLogRequest {

    /**
     * 企业id
     */
    private String enterpriseId;


    /**
     * 日志id
     */
    private Long logId;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 错误信息
     */
    private String errMsg;
}
