package com.coolcollege.intelligent.model.impoetexcel.dto;

import cn.afterturn.easypoi.excel.annotation.Excel;
import lombok.Data;

import java.util.Date;

/**
 * 线索导入
 *
 * @author chenyupeng
 * @since 2021/11/25
 */
@Data
public class EnterpriseCluesImportDTO {

    @Excel(name = "企业名称（必填）", orderNum = "1", width = 20)
    private String name;

    @Excel(name = "门店规模", orderNum = "2", width = 10)
    private Integer storeNum;

    @Excel(name = "联系人名称", orderNum = "3", width = 10)
    private String contact;

    @Excel(name = "联系电话", orderNum = "4", width = 10)
    private String mobile;

    @Excel(name = "企业ID", orderNum = "5", width = 20)
    private String enterpriseId;

    @Excel(name = "省", orderNum = "6", width = 10)
    private String province;

    @Excel(name = "市", orderNum = "7", width = 10)
    private String city;

    @Excel(name = "开通类型", orderNum = "8", width = 10)
    private String appType;

    @Excel(name = "用户类型", orderNum = "9", width = 10)
    private String isVip;

    @Excel(name = "所属行业", orderNum = "10", width = 10)
    private String industry;

    @Excel(name = "认证等级", orderNum = "11", width = 10)
    private String authLevel;

    @Excel(name = "销售阶段", orderNum = "12", width = 10)
    private String salesStage;

    @Excel(name = "套餐开始时间", orderNum = "13", width = 10)
    private String packageBeginDate;

    @Excel(name = "套餐结束时间", orderNum = "14", width = 10)
    private String packageEndDate;

    @Excel(name = "是否付费", orderNum = "15", width = 10)
    private String isPay;
}
