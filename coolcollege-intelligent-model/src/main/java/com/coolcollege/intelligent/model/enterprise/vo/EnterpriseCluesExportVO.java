package com.coolcollege.intelligent.model.enterprise.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * @author chenyupeng
 * @since 2021/11/26
 */
@Data
public class EnterpriseCluesExportVO {

    @Excel(name = "企业名称", width = 20, orderNum = "1")
    private String name;

    @Excel(name = "企业ID", width = 20, orderNum = "2")
    private String enterpriseId;

    @Excel(name = "状态", width = 10, orderNum = "3")
    private String status;

    @Excel(name = "是否个人版", width = 10, orderNum = "4")
    private String isPersonal;

    @Excel(name = "库号", width = 10, orderNum = "5")
    private String dbNameNum;;

    @Excel(name = "省市", width = 10, orderNum = "6")
    private String provinceCity;

    @Excel(name = "开通类型", width = 20, orderNum = "7")
    private String appType;

    @Excel(name = "最近使用任务时间", width = 20, orderNum = "8" , format = "yyyy-MM-dd HH:mm:ss")
    private Date lastUseTaskTime;

    @Excel(name = "任务数", width = 10, orderNum = "9")
    private Integer taskNum;

    @Excel(name = "用户类型", width = 20, orderNum = "10")
    private String isVip;

    @Excel(name = "所属行业", width = 20, orderNum = "11")
    private String industry;

    @Excel(name = "授权人数", width = 20, orderNum = "12")
    private Integer authType;

    @Excel(name = "是否认证", width = 20, orderNum = "13")
    private String isAuthenticated;

    @Excel(name = "认证等级", width = 20, orderNum = "14")
    private String authLevel;

    @Excel(name = "门店规模", width = 20, orderNum = "15")
    private Integer storeNum;

    @Excel(name = "联系人名称", width = 20, orderNum = "16")
    private String contact;

    @Excel(name = "联系电话", width = 20, orderNum = "17")
    private String mobile;

    @Excel(name = "销售阶段", width = 20, orderNum = "18")
    private String salesStage;

    @Excel(name = "是否付费", width = 20, orderNum = "19")
    private String isPay;
}
