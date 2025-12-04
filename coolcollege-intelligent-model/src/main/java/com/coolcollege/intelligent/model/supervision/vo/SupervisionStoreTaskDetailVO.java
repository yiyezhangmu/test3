package com.coolcollege.intelligent.model.supervision.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author suzhuhong
 * @Date 2023/3/1 13:51
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionStoreTaskDetailVO {

    @ApiModelProperty("门店ID")
    private String storeId;
    @ApiModelProperty("门店编号")
    private String storeNum;
    @ApiModelProperty("门店名称")
    private String storeName;
    @ApiModelProperty("执行人ID")
    private String supervisionUserId;
    @ApiModelProperty("提交时间")
    private Date submitTime;
    @ApiModelProperty("门店任务ID")
    private Long id;
    @ApiModelProperty("任务状态")
    private Integer taskState;
    @ApiModelProperty("任务状态中文")
    private String taskStateStr;

    @ApiModelProperty("任务结束时间")
    private Date taskEndTime;

    @ApiModelProperty("待执行次数")
    private Integer count;

    private Integer handleOverTimeStatus;

}
