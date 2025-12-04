package com.coolcollege.intelligent.model.storework.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * @Author suzhuhong
 * @Date 2022/9/26 17:26
 * @Version 1.0
 */
@Data
@ApiModel
public class StoreWorkDataTableVO extends StoreWorkAIFieldVO {

    private Integer tableProperty;

    private Date beginTime;

    private Date endTime;

    private String tableInfo;

    private List<String> handleUserIds;

    private List<String> commentUserIds;

    private List<String> commentUserNames;

    private String actualCommentUserId;

    private String actualCommentUserName;

    private Boolean commentTabDisplayFlag;

    private List<StoreWorkDataTableColumnVO> storeWorkDataTableColumnVOList;

    private Integer completeStatus;

    private Integer commentStatus;
    // 处理权限
    private Boolean handleFlag;

    @ApiModelProperty("日清类型")
    private String workCycle;

}
