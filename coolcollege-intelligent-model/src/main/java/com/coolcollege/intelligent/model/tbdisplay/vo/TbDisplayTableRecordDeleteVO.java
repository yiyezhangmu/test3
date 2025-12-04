package com.coolcollege.intelligent.model.tbdisplay.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;

/**
* @Description:
* @Author:
* @CreateDate: 2021-03-02 17:24:31
*/
@ApiModel
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TbDisplayTableRecordDeleteVO {
    /**
     * 主键id自增
     */
    private Long id;

    /**

    /**
     * 父任务id
     */
    @ApiModelProperty("任务id")
    private Long unifyTaskId;

    /**
     * 门店id
     */
    @ApiModelProperty("门店id")
    private String storeId;


    /**
     * 门店名称
     */
    @ApiModelProperty("门店名称")
    private String storeName;



    /**
     * 删除人id
     */
    @ApiModelProperty("删除人id")
    private String deleteUserId;

    /**
     * 删除人名称
     */
    @ApiModelProperty("删除人名称")
    private String deleteUserName;


    /**
     * 删除时间
     */
    @ApiModelProperty("删除时间")
    private Date deleteTime;
}