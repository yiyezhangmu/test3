package com.coolcollege.intelligent.model.enterprise.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * describe:
 *
 * @author zhouyiping
 * @date 2020/11/13
 */
@Data
public class EnterpriseBossExportDTO {
    /**
     * 企业主键
     */
    @Excel(name = "企业ID",orderNum = "1", width = 10)
    private String id;

    /**
     * 企业名称
     */
    @Excel(name = "企业名称",orderNum = "2", width = 10)
    private String name;


    @Excel(name = "原始企业名称",orderNum = "3", width = 10)
    private String originalName;

    /**
     * 状态-1 已删除  0初始  1正常  100冻结  88创建失败
     */
    @Excel(name = "状态",orderNum = "4", width = 10)
    private String status;

    /**
     * 是否个人版 mainCorpId != null 是  反之否
     */
    @Excel(name = "是否个人版",orderNum = "5", width = 10)
    private String isPersonalVersion;

    /**
     * 库号
     */
    @Excel(name = "库号",orderNum = "6", width = 10)
    private String dbNameNum;

    /**
     * 省市
     */
    @Excel(name = "省市",orderNum = "7", width = 10)
    private String provinceCity;

    /**
     * 开通类型
     */
    @Excel(name = "开通类型",orderNum = "8", width = 10)
    private String openType;

    /**
     * corpid
     */
    @Excel(name = "corpid",orderNum = "9", width = 10)
    private String corpId;

    /**
     * 任务数
     */
    @Excel(name = "任务数",orderNum = "10", width = 10)
    private Integer taskNum;

    /**
     * 最近使用时间
     */
    @Excel(name = "最近使用任务时间",orderNum = "11", width = 10, format = "yyyy-MM-dd HH:mm:ss")
    private Date lastUseTaskTime;

    /**
     * 用户类型(1:普通用户 2:付费用户  3:试用用户 4:共创用户)
     */
    @Excel(name = "用户类型",orderNum = "12", width = 10)
    private String isVip;

    /**
     * 所属行业
     */
    @Excel(name = "所属行业",orderNum = "13", width = 10)
    private String industry;

    /**
     * 授权人数
     */
    @Excel(name = "授权人数",orderNum = "14", width = 10)
    private Integer authType;

    /**
     * 企业是否认证 true 是  false 否
     */
    @Excel(name = "是否认证",orderNum = "15", width = 10)
    private String isAuthenticated;

    /**
     * 企业认证等级，0：未认证，1：高级认证，2：中级认证，3：初级认证
     */
    @Excel(name = "认证等级",orderNum = "16", width = 10)
    private String authLevel;

    /**
     * 套餐开始时间
     */
    @Excel(name = "套餐开始时间",orderNum = "17", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date packageBeginDate;

    /**
     * 套餐结束时间
     */
    @Excel(name = "套餐结束时间",orderNum = "18", width = 20, format = "yyyy-MM-dd HH:mm:ss")
    private Date packageEndDate;

    @Excel(name = "是否留资",orderNum = "19", width = 10)
    private String isLeaveInfo;
}
