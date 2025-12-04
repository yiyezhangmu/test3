package com.coolcollege.intelligent.facade.dto;

import lombok.Data;

import java.util.Date;

/**
 * @Author: huhu
 * @Date: 2025/3/26 15:56
 * @Description:
 */
@Data
public class EnterpriseActivationDTO {

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 企业名称
     */
    private String enterpriseName;

    /**
     * 任务总数
     */
    private Integer taskNum;

    /**
     * 任务门店数
     */
    private Integer storeNum;

    /**
     * 总门店数
     */
    private Integer enterpriseStoreNum;

    /**
     * 企业用户总数
     */
    private Integer enterpriseUserNum;

    /**
     * 未分配人数
     */
    private Integer enterpriseUserUndistributedNum;

    /**
     * 登录人数
     */
    private Integer loginUserNum;

    /**
     * 查询日期
     */
    private Date queryDate;

    /**
     * 查询类型：天：day/周：week
     */
    private String type;

    /**
     * 创建时间
     */
    private Date createTime;
}
