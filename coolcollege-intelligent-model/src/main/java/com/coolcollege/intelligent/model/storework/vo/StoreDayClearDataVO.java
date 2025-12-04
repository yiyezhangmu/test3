package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/23 11:21
 * @Version 1.0
 */
@Data
@ApiModel
public class StoreDayClearDataVO {
    @ApiModelProperty(value = "执行人完成率")
    private String handlerCompleteRate;
    @ApiModelProperty(value = "执行人完成项数")
    private Integer finishColumnNum;
    @ApiModelProperty(value = "总项数")
    private Integer totalColumnNum;

    @ApiModelProperty(value = "完成时间")
    private Date endHandleTime;
    @ApiModelProperty(value = "门店ID")
    private String storeId;
    @ApiModelProperty(value = "门店名称")
    private String storeName;
    @ApiModelProperty(value = "businessId")
    private String businessId;
    @ApiModelProperty(value = "日清类型")
    private String workCycle;
    @ApiModelProperty(value = "当前时间")
    private Date storeWorkDate;
    @ApiModelProperty(value = "店务ID")
    private Long storeWorkId;
    @ApiModelProperty(value = "点评人点评数")
    private Integer commentTableNum;
    @ApiModelProperty(value = "完成表数")
    private Long finishTableNum;
    @ApiModelProperty(value = "总表数")
    private Integer tableNum;
    @ApiModelProperty(value = "店务表信息")
    private List<StoreWorkTableSimpleInfoVO> storeWorkTableInfoList;

}
