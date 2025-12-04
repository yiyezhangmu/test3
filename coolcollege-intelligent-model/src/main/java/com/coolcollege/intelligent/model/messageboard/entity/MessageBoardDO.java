package com.coolcollege.intelligent.model.messageboard.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   wxp
 * @date   2024-07-29 16:11
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageBoardDO implements Serializable {
    @ApiModelProperty("")
    private Long id;

    @ApiModelProperty("业务id")
    private String businessId;

    @ApiModelProperty("业务类型 店务storework 其它other")
    private String businessType;

    @ApiModelProperty("操作类型 留言message 点赞like")
    private String operateType;

    @ApiModelProperty("留言内容")
    private String messageContent;

    @ApiModelProperty("点赞数")
    private Integer likeCount;

    @ApiModelProperty("发起人")
    private String createUserId;

    @ApiModelProperty("更新人")
    private String updateUserId;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("删除标识")
    private Boolean deleted;
}