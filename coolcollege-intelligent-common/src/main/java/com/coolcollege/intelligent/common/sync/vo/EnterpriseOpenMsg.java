package com.coolcollege.intelligent.common.sync.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 企业开通相关的参数
 * @author ：xugangkun
 * @date ：2022/2/11 14:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnterpriseOpenMsg {

    /**
     * 企业id
     */
    private String eid;

    /**
     * corpId
     */
    private String corpId;

    /**
     * 开通类型
     */
    private String appType;

    /**
     * 授权用户id
     */
    private String authUserId;

    /**
     * 数据库名
     */
    private String dbName;

}
