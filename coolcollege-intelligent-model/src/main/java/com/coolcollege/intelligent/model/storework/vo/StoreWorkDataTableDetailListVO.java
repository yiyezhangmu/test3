package com.coolcollege.intelligent.model.storework.vo;

import com.coolcollege.intelligent.model.storework.dto.StoreWorkCommonDTO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * @author wxp
 */
@ApiModel
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StoreWorkDataTableDetailListVO {

    @ApiModelProperty("店务id")
    private Long storeWorkId;

    @ApiModelProperty("店务周期 DAY:天 WEEK:周 MONTH:月")
    private String workCycle;

    @ApiModelProperty("店务日期 月2022-08-01 周2022-08-01 日2022-08-02")
    private Date storeWorkDate;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("完成率")
    private BigDecimal finishPercent;

    @ApiModelProperty("应完成项")
    private Integer totalColumnNum;

    @ApiModelProperty("未完成项")
    private Integer unFinishColumnNum;

    @ApiModelProperty("已完成项")
    private Integer finishColumnNum;

    @ApiModelProperty("数据表的ID")
    private Long dataTableId;

    @ApiModelProperty("检查表的ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("作业开始时间")
    private Date beginTime;

    @ApiModelProperty("作业结束时间")
    private Date endTime;

    @ApiModelProperty("执行人列表")
    private List<StoreWorkCommonDTO> personList;

    /**
     * 检查时间
     */
    private String checkTime;

}
