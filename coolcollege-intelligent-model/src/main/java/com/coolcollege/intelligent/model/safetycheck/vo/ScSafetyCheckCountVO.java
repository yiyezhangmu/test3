package com.coolcollege.intelligent.model.safetycheck.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.math.BigDecimal;

/**
 * @author byd
 * @date 2023-08-17 14:28
 */
@ApiModel
@Data
public class ScSafetyCheckCountVO {

    @ApiModelProperty("员工id")
    private String userId;

    @Excel(name = "员工姓名", orderNum = "1")
    @ApiModelProperty("员工姓名")
    private String userName;

    @Excel(name = "已巡门店数", orderNum = "2")
    @ApiModelProperty("已巡门店数")
    private Integer patrolStoreNum;

    @Excel(name = "门店平均得分", orderNum = "3")
    @ApiModelProperty("门店平均得分")
    private BigDecimal storeAvgScore;

    @Excel(name = "90分以上门店数", orderNum = "4")
    @ApiModelProperty("90分以上门店数")
    private Long ninetyScoreStoreNum;

    @Excel(name = "80分到89门店数", orderNum = "5")
    @ApiModelProperty("80分到89门店数")
    private Long eightyScoreStoreNum;

    @Excel(name = "80分一下门店数", orderNum = "6")
    @ApiModelProperty("80分一下门店数")
    private Long eightyDownScoreStoreNum;

    @Excel(name = "审批是拒绝次数", orderNum = "7")
    @ApiModelProperty("审批是拒绝次数")
    private Long auditRejectNum;

    @Excel(name = "门店申诉通过次数", orderNum = "8")
    @ApiModelProperty("门店申诉通过次数")
    private Long storeAppealPassNum;

    @Excel(name = "门店申诉拒绝次数", orderNum = "9")
    @ApiModelProperty("门店申诉拒绝次数")
    private Long storeAppealRejectNum;
}
