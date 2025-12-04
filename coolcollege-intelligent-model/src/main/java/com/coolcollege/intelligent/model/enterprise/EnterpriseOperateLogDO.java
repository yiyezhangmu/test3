package com.coolcollege.intelligent.model.enterprise;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
* @Description:
* @Author: wxp
* @CreateDate: 2021-03-27 10:49:25
*/
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class EnterpriseOperateLogDO {
    /**
     * ID
     */
    private Long id;

    /**
     * 企业Id
     */
    private String enterpriseId;

    /**
     * 操作类型 open close sync
     */
    private String operateType;

    /**
     * 操作描述
     */
    private String operateDesc;

    /**
     * 同步状态 0未开启同步 1同步中 2同步成功 3同步失败
     */
    private Integer status;

    /**
     * 操作开始时间
     */
    private Date operateStartTime;

    /**
     * 操作结束时间
     */
    private Date operateEndTime;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名
     */
    private String userName;

    private String remark;

}