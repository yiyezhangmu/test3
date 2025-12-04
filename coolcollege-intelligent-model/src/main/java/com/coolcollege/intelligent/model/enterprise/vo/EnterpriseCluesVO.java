package com.coolcollege.intelligent.model.enterprise.vo;

import lombok.Data;

import java.util.Date;

/**
 * 企业线索
 *
 * @author chenyupeng
 * @since 2021/11/23
 */
@Data
public class EnterpriseCluesVO {

    /**
     * 主键
     */
    private Long id;

    /**
     * 企业id
     */
    private String enterpriseId;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 原始名称
     */
    private String originalName;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 用户类型(1:普通用户 2:付费用户  3:试用用户 4:共创用户)
     */
    private Integer isVip;

    /**
     * 授权人数
     */
    private Integer authType;

    private String authUserId;

    private String industry;

    /**
     * 企业是否认证
     */
    private Boolean isAuthenticated;

    /**
     * 企业认证等级，0：未认证，1：高级认证，2：中级认证，3：初级认证
     */
    private Integer authLevel;

    /**
     * 注册时间
     */
    private Date createTime;

    /**
     * 最后一次更新时间
     */
    private Date updateTime;


    /**
     * 套餐开始时间
     */
    private Date packageBeginDate;

    /**
     * 套餐结束时间
     */
    private Date packageEndDate;

    /**
     * 来源类型：默认dingding钉钉,qw企业微信 mobile
     */
    private String appType;

    /**
     * 创建人id
     */
    private String createId;

    /**
     * 创建人名称
     */
    private String createName;

    /**
     * 修改人id
     */
    private String updateId;

    /**
     * 修改人名称
     */
    private String updateName;

    /**
     * 权限，逗号分隔
     */
    private String userIds;

    /**
     * 销售阶段
     */
    private Integer salesStage;

    /**
     * 门店规模
     */
    private Integer storeNum;

    private Date lastUseTaskTime;

    private Integer taskNum;

    /**
     * 库号
     */
    private String dbNameNum;

    /**
     * 不为空就是个人版
     */
    private String mainCorpId;

    private String provinceCity;

    /**
     * 是否付费
     */
    private Boolean isPay;

    private String contact;
}
