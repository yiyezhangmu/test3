package com.coolcollege.intelligent.model.enterprise.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

@Data
public class EnterpriseActivationVO {

    @Excel(name = "企业ID",orderNum = "1", width = 10)
    @ApiModelProperty("企业id")
    private String enterpriseId;

    @Excel(name = "企业名称",orderNum = "1", width = 10)
    @ApiModelProperty("企业名称")
    private String enterpriseName;

    @Excel(name = "csm",orderNum = "1", width = 10)
    @ApiModelProperty("csm")
    private String csm;

    @Excel(name = "查询日期",orderNum = "1", width = 10, format = "yyyy-MM-dd")
    @ApiModelProperty("查询日期")
    private Date queryDate;

    @Excel(name = "统计维度",orderNum = "1", width = 10)
    @ApiModelProperty("统计维度：天:day，周:week，月:month")
    private String type;

    @Excel(name = "总门店数",orderNum = "1", width = 10)
    @ApiModelProperty("总门店数")
    private Integer totalStoreNum;

    @Excel(name = "营业门店数",orderNum = "1", width = 10)
    @ApiModelProperty("营业门店数")
    private Integer totalStoreOpenNum;

    @Excel(name = "企业用户总数",orderNum = "1", width = 10)
    @ApiModelProperty("企业用户总数")
    private Integer totalUserNum;

    @Excel(name = "登录人数",orderNum = "1", width = 10)
    @ApiModelProperty("登录人数")
    private Integer loginUserNum;

    @Excel(name = "未分配人数",orderNum = "1", width = 10)
    @ApiModelProperty("未分配人数")
    private Integer noRoleUserNum;

    @Excel(name = "巡店总次数",orderNum = "1", width = 10)
    @ApiModelProperty("巡店总次数")
    private Integer totalPatrolNum;

    @Excel(name = "巡店总门店数",orderNum = "1", width = 10)
    @ApiModelProperty("巡店总门店数")
    private Integer totalPatrolStoreNum;

    @Excel(name = "巡店完成总数",orderNum = "1", width = 10)
    @ApiModelProperty("巡店完成总数")
    private Integer patrolStoreFinishNum;

    @Excel(name = "巡店覆盖率",orderNum = "1", width = 10)
    @ApiModelProperty("巡店覆盖率")
    private String patrolStoreCoverRate;

    @Excel(name = "工单总数",orderNum = "1", width = 10)
    @ApiModelProperty("工单总数")
    private Integer totalQuestionNum;

    @Excel(name = "工单总门店数",orderNum = "1", width = 10)
    @ApiModelProperty("工单总门店数")
    private Integer totalQuestionStoreNum;

    @Excel(name = "工单完成总数",orderNum = "1", width = 10)
    @ApiModelProperty("工单完成总数")
    private Integer questionFinishNum;

    @Excel(name = "工单完成率",orderNum = "1", width = 10)
    @ApiModelProperty("工单完成率")
    private String questionFinishRate;

    @Excel(name = "陈列总数",orderNum = "1", width = 10)
    @ApiModelProperty("陈列总数")
    private Integer totalDisplayNum;

    @Excel(name = "陈列总门店数",orderNum = "1", width = 10)
    @ApiModelProperty("陈列总门店数")
    private Integer totalDisplayStoreNum;

    @Excel(name = "陈列完成数",orderNum = "1", width = 10)
    @ApiModelProperty("陈列完成数")
    private Integer displayFinishNum;

    @Excel(name = "陈列完成率",orderNum = "1", width = 10)
    @ApiModelProperty("陈列完成率")
    private String displayFinishRate;

    @Excel(name = "店务总数",orderNum = "1", width = 10)
    @ApiModelProperty("店务总数")
    private Integer totalStoreWorkNum;

    @Excel(name = "店务总门店数",orderNum = "1", width = 10)
    @ApiModelProperty("店务总门店数")
    private Integer totalStoreWorkStoreNum;

    @Excel(name = "店务完成数",orderNum = "1", width = 10)
    @ApiModelProperty("店务完成数")
    private Integer storeWorkFinishNum;

    @Excel(name = "店务完成率",orderNum = "1", width = 10)
    @ApiModelProperty("店务完成率")
    private String storeWorkFinishRate;

    public String getDisplayFinishRate() {
        if (displayFinishNum != null && totalDisplayNum != null && totalDisplayNum != 0) {
            displayFinishRate = String.format("%.2f%%", displayFinishNum * 100.0 / totalDisplayNum);
        }
        return displayFinishRate;
    }

    public String getQuestionFinishRate() {
        if (questionFinishNum != null && totalQuestionNum != null && totalQuestionNum != 0) {
            questionFinishRate = String.format("%.2f%%", questionFinishNum * 100.0 / totalQuestionNum);
        }
        return questionFinishRate;
    }

    public String getPatrolStoreCoverRate() {
        if (totalPatrolStoreNum != null && totalStoreNum != null && totalStoreNum != 0) {
            patrolStoreCoverRate = String.format("%.2f%%", totalPatrolStoreNum * 100.0 / totalStoreNum);
        }
        return patrolStoreCoverRate;
    }

    public String getStoreWorkFinishRate() {
        if (storeWorkFinishNum != null && totalStoreWorkNum != null && totalStoreWorkNum != 0) {
            storeWorkFinishRate = String.format("%.2f%%", storeWorkFinishNum * 100.0 / totalStoreWorkNum);
        }
        return storeWorkFinishRate;
    }
}
