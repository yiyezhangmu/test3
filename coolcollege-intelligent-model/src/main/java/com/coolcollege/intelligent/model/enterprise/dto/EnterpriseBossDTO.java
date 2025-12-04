package com.coolcollege.intelligent.model.enterprise.dto;

import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/13
 */
@Data
public class EnterpriseBossDTO {
    /**
     * 企业主键
     */
    private String id;

    /**
     * 企业名称
     */
    private String name;

    /**
     * 套餐id
     */
    private Long currentPackageId;

    /**
     * 套餐
     */
    private String currentPackageName;

    /**
     * 原始名称
     */
    private String originalName;

    /**
     * 电话
     */
    private String mobile;

    /**
     * 省
     */
    private String province;

    /**
     * 市
     */
    private String city;

    /**
     * 状态
     */
    private Integer status;

    private String logo;

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
     * 企业logo
     */
    private String corpLogoUrl;

    /**
     * logo名称
     */
    private String logoName;

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
     * 阿里云分组cropId
     */
    private String groupCropId;
    /**
     * 企业定cropId
     */
    private String dingCorpId;

    /**
     * 数据库名称
     */
    private String dbName;

    /**
     * 数据库服务器
     */
    private String dbServer;
    /**
     * 数据库库号
     */
    private String dbNameNum;

    private String mainCorpId;

    private Integer videoCount;

    private Integer storeCount;

    private String appType;

    private String tag;

    /**
     * 是否留资
     */
    private Boolean isLeaveInfo;
    private String leaveUserName;
    private String leaveMobile;
    private Date leaveTime;

    private Integer limitStoreCount;

    private Integer limitDeviceCount;

    private String coolCollegeEnterpriseId;




}
