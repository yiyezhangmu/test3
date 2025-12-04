package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author wxp
 * @Date 2022/11/11 11:22
 * @Version 1.0
 */
@Data
public class StoreWorkDataTableSimpleVO {

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("店务记录表tc_business_id")
    private String tcBusinessId;

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


    @ApiModelProperty("作业开始时间")
    private Date beginTime;

    @ApiModelProperty("作业结束时间")
    private Date endTime;

    @ApiModelProperty("检查表ID")
    private Long metaTableId;

    @ApiModelProperty("检查表名称")
    private String tableName;

    @ApiModelProperty("完成状态 0:未完成  1:已完成")
    private Integer completeStatus;

    @ApiModelProperty("点评状态 0:未点评  1:已点评")
    private Integer commentStatus;

}
