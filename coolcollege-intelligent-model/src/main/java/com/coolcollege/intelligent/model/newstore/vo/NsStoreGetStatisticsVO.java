package com.coolcollege.intelligent.model.newstore.vo;

import cn.afterturn.easypoi.excel.annotation.Excel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 新店分析表VO
 * @author zhangnan
 * @date 2022-03-08 11:26
 */
@Data
public class NsStoreGetStatisticsVO {

    @Excel(name = "门店类型", orderNum = "1")
    @ApiModelProperty("新店类型")
    private String newStoreType;

    @Excel(name = "门店数量", orderNum = "2", groupName = "进行中新店")
    @ApiModelProperty("进行中-新店数量")
    private Integer ongoingStoreNum;

    @Excel(name = "拜访次数", orderNum = "3", groupName = "进行中新店")
    @ApiModelProperty("进行中-拜访次数")
    private Integer ongoingVisitNum;

    @Excel(name = "门店数量", orderNum = "4", groupName = "完成新店")
    @ApiModelProperty("已完成-新店数量")
    private Integer completedStoreNum;

    @Excel(name = "拜访次数", orderNum = "5", groupName = "完成新店")
    @ApiModelProperty("已完成-拜访次数")
    private Integer completedVisitNum;

    @Excel(name = "门店数量", orderNum = "6", groupName = "失败新店")
    @ApiModelProperty("失败-新店数量")
    private Integer failedStoreNum;

    @Excel(name = "拜访次数", orderNum = "7", groupName = "失败新店")
    @ApiModelProperty("失败-拜访次数")
    private Integer failedVisitNum;

}
