package com.coolcollege.intelligent.model.unifytask.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author zhangchenbiao
 * @FileName: TaskFinishStoreVO
 * @Description:
 * @date 2024-10-17 14:39
 */
@Data
public class TaskFinishStoreVO {

    @ApiModelProperty("任务id")
    private Long unifyTaskId;

    @ApiModelProperty("门店id")
    private String storeId;

    @ApiModelProperty("门店名称")
    private String storeName;

    @ApiModelProperty("循环任务")
    private Integer loopCount;

}
