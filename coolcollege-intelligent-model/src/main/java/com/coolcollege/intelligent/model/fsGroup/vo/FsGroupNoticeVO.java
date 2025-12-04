package com.coolcollege.intelligent.model.fsGroup.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 群公告表(FsGroupNotice)实体类
 *
 * @author CFJ
 * @since 2024-05-06 11:32:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FsGroupNoticeVO implements Serializable {
    @ApiModelProperty("id")
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
    @ApiModelProperty("封面图片")
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
     * 跳转链接
     */
    @ApiModelProperty("跳转链接")
    private String detailUrl;
    /**
     * 公告详情类型1:自定义内容 2:链接地址
     */
    @ApiModelProperty("公告详情类型1:自定义内容 2:链接地址")
    private String detailType;
    /**
     * 发送门店群id，null为发送全部
     */
    @ApiModelProperty("发送门店群的区域ids")
    private String sendStoreRegionIds;
    /**
     * 同上
     */
    @ApiModelProperty("发送区域群chatIds")
    private String sendRegionChatIds;
    /**
     * 同上
     */
    @ApiModelProperty("发送其他群的chatIds")
    private String sendOtherChatIds;

    @ApiModelProperty("发送门店群的区域")
    private List<IdAndNameVO> sendStoreRegionList;
    @ApiModelProperty("发送区域群List")
    private List<IdAndNameVO> sendRegionChatList;
    @ApiModelProperty("发送其他群的List")
    private List<IdAndNameVO> sendOtherChatList;
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

    @ApiModelProperty("创建人名")
    private String createUserName;
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

    @ApiModelProperty("发送群数量")
    private Integer groupCount;

}

