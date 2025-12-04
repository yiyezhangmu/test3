package com.coolcollege.intelligent.model.enterprise;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * 企业mq配置表(EnterpriseMqInformConfig)实体类
 *
 * @author CFJ
 * @since 2023-08-03 13:39:05
 */
@Data
@Builder
public class EnterpriseMqInformConfigDO implements Serializable {
    private static final long serialVersionUID = 259105418431929101L;
    /**
     * 消息通知地址
     */
    private String url;
    /**
     * 企业id
     */
    private String enterpriseId;
    /**
     * 是否开启消息推送 0否 1是
     */
    private Integer status;
    /**
     * md5key
     */
    private String md5Key;

}

