package com.coolcollege.intelligent.model.fsGroup;

import java.util.Date;
import java.io.Serializable;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 群公告表(FsGroupNotice)实体类
 *
 * @author CFJ
 * @since 2024-05-10 10:36:43
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupNoticeDO implements Serializable {
    private static final long serialVersionUID = -48542023110681671L;
        
    @ApiModelProperty("")
    private Long id;
    /**
     * 公告名称
     */    
    @ApiModelProperty("公告名称")
    private String name;
    /**
     * 封面图片
     */    
    @ApiModelProperty("封面图片")
    private String img;
    /**
     * 封面图片
     */
    @ApiModelProperty("封面图片Url")
    private String imgUrl;
    /**
     * 公告摘要
     */    
    @ApiModelProperty("公告摘要")
    private String content;
    /**
     * 公告详情
     */    
    @ApiModelProperty("公告详情")
    private String detail;
    /**
     * 公告详情类型1:自定义内容 2:自定义链接
     */    
    @ApiModelProperty("公告详情类型1:自定义内容 2:自定义链接")
    private String detailType;
    /**
     * 跳转链接
     */    
    @ApiModelProperty("跳转链接")
    private String detailUrl;
    /**
     * 发送门店群id，null为发送全部
     */    
    @ApiModelProperty("发送门店群id，null为发送全部")
    private String sendStoreRegionIds;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String sendRegionChatIds;
    /**
     * 群id
     */    
    @ApiModelProperty("群id")
    private String sendOtherChatIds;
    /**
     * 1:立即发送2:定时发送
     */    
    @ApiModelProperty("1:立即发送2:定时发送")
    private String sendTimeType;
    /**
     * 发送人数
     */    
    @ApiModelProperty("发送人数")
    private Integer sendUserCount;
    /**
     * 已读人数
     */    
    @ApiModelProperty("已读人数")
    private Integer hasReadCount;
    /**
     * 0:未发送1:已发送
     */    
    @ApiModelProperty("0:未发送1:已发送")
    private String hasSend;
    /**
     * 发送时间
     */    
    @ApiModelProperty("发送时间")
    private Date sendTime;
    /**
     * 创建人
     */    
    @ApiModelProperty("创建人")
    private String createUserId;
    /**
     * 创建时间
     */    
    @ApiModelProperty("创建时间")
    private Date createTime;
    /**
     * 更新时间
     */    
    @ApiModelProperty("更新时间")
    private Date updateTime;
    /**
     * 更新人
     */    
    @ApiModelProperty("更新人")
    private String updateUser;

}

