package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/10/13 11:22
 * @Version 1.0
 */
@Data
public class StoreWorkDataTableDetailVO extends StoreWorkAIFieldVO {

    @ApiModelProperty("默认选择 0:默认不选中, 1:默认选中合格, 2:默认选中不合格，3 不适用")
    private Integer defaultResultColumn;

    private Integer tableProperty;

    private String storeName;

    private Integer commentStatus;

    private String actualHandleUserId;

    private String actualHandleUserName;
    @ApiModelProperty("执行时间")
    private Date beginHandleTime;
    @ApiModelProperty("完成时间")
    private Date endHandleTime;

    private String tableName;
    @ApiModelProperty("开始时间")
    private Date beginTime;
    @ApiModelProperty("结束时间")
    private Date endTime;

    private String tableInfo;


    List<StoreWorkDataTableColumnVO> storeWorkDataTableColumnVOS;

    /**
     * 同去点评按钮判断逻辑相同，用于点评页面判断是否能够进行点评（因为存在AI分析完成但是点评人能够二次点评的情况）
     */
    private Boolean commentTabDisplayFlag;
}
