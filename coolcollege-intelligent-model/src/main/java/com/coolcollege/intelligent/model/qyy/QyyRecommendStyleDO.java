package com.coolcollege.intelligent.model.qyy;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @author   zhangchenbiao
 * @date   2023-04-11 03:48
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QyyRecommendStyleDO implements Serializable {
    @ApiModelProperty("自增id")
    private Long id;

    @ApiModelProperty("商品名称")
    private String name;

    @ApiModelProperty("关联商品个数")
    private Integer goodsNum;

    @ApiModelProperty("关联商品ids")
    private String goodsIds;

    @ApiModelProperty("发送时间")
    private Date sendTime;

    @ApiModelProperty("发送状态，0:未发送;1:已发送")
    private Integer sendStatus;

    @ApiModelProperty("0:立即发送/1:定时发送")
    private Integer sendType;

    @ApiModelProperty("门店群")
    private String storeConversation;

    @ApiModelProperty("分公司群")
    private String compConversation;

    @ApiModelProperty("其他群")
    private String otherConversation;

    @ApiModelProperty("分公司群开放群id")
    private String compConversationId;

    @ApiModelProperty("其他群开放群id")
    private String otherConversationId;

    @ApiModelProperty("删除")
    private Boolean deleted;

    @ApiModelProperty("创建人id")
    private String createUserId;

    @ApiModelProperty("创建人名称")
    private String createUserName;

    @ApiModelProperty("创建人id")
    private String updateUserId;

    @ApiModelProperty("创建人名称")
    private String updateUserName;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("更新时间")
    private Date updateTime;

    @ApiModelProperty("课程信息")
    private String courseInfo;

    @ApiModelProperty("群信息")
    private String conversationInfo;
}