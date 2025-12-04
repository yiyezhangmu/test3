package com.coolcollege.intelligent.model.supervision.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author suzhuhong
 * @Date 2023/4/13 19:48
 * @Version 1.0
 */
@Data
@ApiModel
public class SupervisionReassignStoreVO {

    @ApiModelProperty("门店ID")
    private String storeId;
    @ApiModelProperty("门店名称")
    private String storeName;
    @ApiModelProperty("按门店任务ID")
    private Long supervisionStoreTaskId;

}
